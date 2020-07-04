import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.metrics import pairwise_distances
from firebase import Firebase

firebaseConfig = {
    "apiKey": "AIzaSyD1G73URmWIpDCO7ko749dNINP23pTYtmI",
    "authDomain": "forevents-2c0e8.firebaseapp.com",
    "databaseURL": "https://forevents-2c0e8.firebaseio.com",
    "projectId": "forevents-2c0e8",
    "storageBucket": "forevents-2c0e8.appspot.com",
    "messagingSenderId": "958196478519",
    "appId": "1:958196478519:web:d629f424d4ec7c05d9c5b2"
  }

firebase = Firebase(firebaseConfig)

db = firebase.database()

events = pd.read_csv("events.csv",encoding="Latin1")
Ratings = pd.read_csv("ratings.csv")


Mean = Ratings.groupby(by="userId",as_index=False)['rating'].mean()
Rating_avg = pd.merge(Ratings,Mean,on='userId')
Rating_avg['adg_rating']=Rating_avg['rating_x']-Rating_avg['rating_y']

check = pd.pivot_table(Rating_avg,values='rating_x',index='userId',columns='eventId')
check.head()

final = pd.pivot_table(Rating_avg,values='adg_rating',index='userId',columns='eventId')
final.head()

# Reemplazando NAN por eventos promedio
final_event = final.fillna(final.mean(axis=0))

# Reemplazando NAN por usuarios promedio
final_user = final.apply(lambda row: row.fillna(row.mean()), axis=1)

# usuarios similares reemplazando NAN por eventos avg
cosine = cosine_similarity(final_event)
np.fill_diagonal(cosine, 0 )
similarity_with_event = pd.DataFrame(cosine,index=final_event.index)
similarity_with_event.columns=final_user.index
similarity_with_event.head()


def buscar_n_vecinos(df,n):
    order = np.argsort(df.values, axis=1)[:, :n]
    df = df.apply(lambda x: pd.Series(x.sort_values(ascending=False)
           .iloc[:n].index, 
          index=['top{}'.format(i) for i in range(1, n+1)]), axis=1)
    return df


# top 30 vecinos por eventos
sim_user_30_m = buscar_n_vecinos(similarity_with_event,30)
sim_user_30_m.head()


Rating_avg = Rating_avg.astype({"eventId": str})
event_user = Rating_avg.groupby(by = 'userId')['eventId'].apply(lambda x:','.join(x))


def recomendar(user):
    evento_visto_por_usuario = check.columns[check[check.index==user].notna().any()].tolist()
    a = sim_user_30_m[sim_user_30_m.index==user].values
    b = a.squeeze().tolist()
    d = event_user[event_user.index.isin(b)]
    l = ','.join(d.values)
    evento_visto_por_usuario_similar = l.split(',')
    eventos_considerados = list(set(evento_visto_por_usuario_similar)-set(list(map(str, evento_visto_por_usuario))))
    eventos_considerados = list(map(str, eventos_considerados))
    score = []
    for item in eventos_considerados:
        c = final_event.loc[:,item]
        d = c[c.index.isin(b)]
        f = d[d.notnull()]
        avg_user = Mean.loc[Mean['userId'] == user,'rating'].values[0]
        index = f.index.values.squeeze().tolist()
        corr = similarity_with_event.loc[user,index]
        fin = pd.concat([f, corr], axis=1)
        fin.columns = ['adg_score','correlation']
        fin['score']=fin.apply(lambda x:x['adg_score'] * x['correlation'],axis=1)
        nume = fin['score'].sum()
        deno = fin['correlation'].sum()
        final_score = avg_user + (nume/deno)
        score.append(final_score)
    data = pd.DataFrame({'eventId':eventos_considerados,'score':score})
    top_5_recomendaciones = data.sort_values(by='score',ascending=False).head(5)
    event_id = top_5_recomendaciones.merge(events, how='inner', on='eventId')
    event_ids = event_id.eventId.values.tolist()
    return event_ids


userid = (Mean['userId'])
for element in userid: 
    print("Recomendacion para: "+element)
    predicted_events = recomendar(element)  
    for i in predicted_events:
        data = {i: "true"}
        db.child("Recommendations/").child(element).child(i).set(True)
        print(i)
