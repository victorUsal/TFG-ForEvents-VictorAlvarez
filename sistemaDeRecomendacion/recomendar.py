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

# Replacing NaN by event Average
final_event = final.fillna(final.mean(axis=0))

# Replacing NaN by user Average
final_user = final.apply(lambda row: row.fillna(row.mean()), axis=1)


# user similarity on replacing NAN by user avg
b = cosine_similarity(final_user)
np.fill_diagonal(b, 0 )
similarity_with_user = pd.DataFrame(b,index=final_user.index)
similarity_with_user.columns=final_user.index
similarity_with_user.head()

# user similarity on replacing NAN by item(event) avg
cosine = cosine_similarity(final_event)
np.fill_diagonal(cosine, 0 )
similarity_with_event = pd.DataFrame(cosine,index=final_event.index)
similarity_with_event.columns=final_user.index
similarity_with_event.head()


def find_n_neighbours(df,n):
    order = np.argsort(df.values, axis=1)[:, :n]
    df = df.apply(lambda x: pd.Series(x.sort_values(ascending=False)
           .iloc[:n].index, 
          index=['top{}'.format(i) for i in range(1, n+1)]), axis=1)
    return df


# top 30 neighbours for each user
sim_user_30_u = find_n_neighbours(similarity_with_user,30)
sim_user_30_u.head()

sim_user_30_m = find_n_neighbours(similarity_with_event,30)
sim_user_30_m.head()

def get_user_similar_events( user1, user2 ):
    common_events = Rating_avg[Rating_avg.userId == user1].merge(
    Rating_avg[Rating_avg.userId == user2],
    on = "eventId",
    how = "inner" )
    return common_events.merge( events, on = 'eventId' )

    
a = get_user_similar_events('t8EGuWbIeCTPvHLVO2Kryfl8FyI2','ANqoJa9z1hOu0QXWg00uI5Lbbg82')
a = a.loc[ : , ['rating_x_x','rating_x_y','title']]
a.head()



Rating_avg = Rating_avg.astype({"eventId": str})
event_user = Rating_avg.groupby(by = 'userId')['eventId'].apply(lambda x:','.join(x))



def User_item_score1(user):
    event_seen_by_user = check.columns[check[check.index==user].notna().any()].tolist()
    a = sim_user_30_m[sim_user_30_m.index==user].values
    b = a.squeeze().tolist()
    d = event_user[event_user.index.isin(b)]
    l = ','.join(d.values)
    event_seen_by_similar_users = l.split(',')
    events_under_consideration = list(set(event_seen_by_similar_users)-set(list(map(str, event_seen_by_user))))
    events_under_consideration = list(map(str, events_under_consideration))
    score = []
    for item in events_under_consideration:
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
    data = pd.DataFrame({'eventId':events_under_consideration,'score':score})
    top_5_recommendation = data.sort_values(by='score',ascending=False).head(5)
    event_Name = top_5_recommendation.merge(events, how='inner', on='eventId')
    event_Names = event_Name.eventId.values.tolist()
    return event_Names

userid = (Mean['userId'])
for element in userid: 
    print("Recomendacion para: "+element)
    predicted_events = User_item_score1(element)  
    for i in predicted_events:
        data = {i: "true"}
        db.child("Recommendations/").child(element).child(i).set(True)
        print(i)
