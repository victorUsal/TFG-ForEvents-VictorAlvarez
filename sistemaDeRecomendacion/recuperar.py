from firebase import Firebase
import csv
import json
import pandas as pd

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
all_users = db.child("Users").get()
all_events = db.child("Posts").get()
all_ratings = db.child("Rating").get()

with open('Usuarios.json', 'w') as file:
    json.dump(all_users.val(), file, indent=4)

with open('Eventos.json', 'w') as file:
    json.dump(all_events.val(), file, indent=4)

with open('Ratings.json','w')as file:
    json.dump(all_ratings.val(),file, indent=4)


df = pd.read_json (r'C:/Users/tfg/Desktop/Definitivo/python/sistemaDeRecomendacion/Usuarios.json')
f = csv.writer(open("users.csv", "w", newline=''))
f.writerow(["email", "username", "name", "userId"])

for user in all_users.each():
    user_info = db.child("Users/"+user.key()).get()
    f.writerow([df[user_info.key()]['email'],
                df[user_info.key()]['username'],
                df[user_info.key()]['name'],
                df[user_info.key()]['id']])

de = pd.read_json (r'C:/Users/tfg/Desktop/Definitivo/python/sistemaDeRecomendacion/Eventos.json')
fi = csv.writer(open("events.csv", "w", newline=''))
fi.writerow(["title","eventId"])

for event in all_events.each():
    event_info = db.child("Posts/"+event.key()).get()
    fi.writerow([de[event_info.key()]['nombre'],
                de[event_info.key()]['postid']])


dr = pd.read_json (r'C:/Users/tfg/Desktop/Definitivo/python/sistemaDeRecomendacion/Ratings.json')
fr = csv.writer(open("ratings.csv", "w", newline=''))
fr.writerow(["userId","eventId","rating"])

for rating in all_ratings.each():
    rating_info = db.child("Rating/"+rating.key()).get()
    for prueba in rating_info.each():
        prueba_info = db.child("Rating/"+rating.key()+"/"+prueba.key()).get()
        fr.writerow([dr[rating_info.key()][prueba_info.key()]['user'],
                    dr[rating_info.key()][prueba_info.key()]['postId'],
                    dr[rating_info.key()][prueba_info.key()]['rateValue']])
