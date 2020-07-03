const functions = require('firebase-functions');
const rp = require('request-promise');

var admin = require("firebase-admin");

var serviceAccount = require("./service_key.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://forevents-2c0e8.firebaseio.com"
});

let db = admin.database();

 exports.obtenerTicketMaster = functions.https.onRequest((request, res) => {
    
    const url = 'https://app.ticketmaster.com/discovery/v2/events.json?countryCode=ES&size=200&apikey=kgSr5xaomFtn8jfolE3pa5qsonGvqXBO&page=1';
    
    var options = {
        uri: url,
        method: "GET",
        json: true 
    };
     
    rp(options)
        .then(function (response) {

            let eventos = response._embedded.events;
            for(var i = 0; i<eventos.length; i++){
          
              if(eventos[i].dates.start.localTime){
              var horaInicio = eventos[i].dates.start.localTime;
              horaInicio = horaInicio.split(":");
              var hInicio = horaInicio[0] +":"+ horaInicio[1];
              }else{
                 hInicio = "00:00"
              }
          
              var eInicio = eventos[i].sales.public.startDateTime;
                eInicio = eInicio.replace("T", " a las ");
                eInicio = eInicio.replace("Z", " ");
          
               var eFin = eventos[i].sales.public.endDateTime
                eFin = eFin.replace("T", " a las ");
                eFin = eFin.replace("Z", " ");
          
              var descripcion = "Evento proporcionado por Ticketmaster: Venta de entradas disponibles entre " + eInicio + " y " + eFin + "Evento : " + eventos[i].classifications[0].genre.name + " / "+ eventos[i].classifications[0].subGenre.name +" Organizado por : " + eventos[i].promoter.name + "Obten tus entradas en: "+ eventos[i].url;
          
              
          
              if(eventos[i]._embedded.venues[0].address){   
                    var ubicacion = eventos[i]._embedded.venues[0].name + ", " +  eventos[i]._embedded.venues[0].address.line1 + ", "+ eventos[i]._embedded.venues[0].city.name + ", "+ eventos[i]._embedded.venues[0].state.name + ", "+ eventos[i]._embedded.venues[0].country.name;
              }else{
                    ubicacion = eventos[i]._embedded.venues[0].name +", "+ eventos[i]._embedded.venues[0].city.name + ", "+ eventos[i]._embedded.venues[0].state.name + ", "+ eventos[i]._embedded.venues[0].country.name;
              }

                var categoria = "";
                var tematica= "";
                if(eventos[i].classifications[0].segment){
                  categoria = eventos[i].classifications[0].segment.name;
                  if(categoria === "Miscellaneous"){
                  tematica = "Ocio";
                  }else if(categoria === "Sports"){
                    tematica = "Ocio";
                  }else if(categoria === "Music"){
                    tematica = "Ocio";
                  }else if(categoria === "Arts & Theatre"){
                    tematica = "Cultural";
                  }else if(categoria === "Undefined"){
                    tematica = "Ocio";
                  }else if(categoria === "Film"){
                    tematica = "Cultural";
                  }else{
                    tematica="Ocio";
                  }

                }
          

                 var post = {
                      'aforo': 'Sin determinar',
                      'descripcion': descripcion,
                      'fechaFin': 'Sin determinar',
                      'fechaInicio' : eventos[i].dates.start.localDate,
                      'horaFin': '00:00',
                      'horaInicio' : hInicio,
                      'nombre': eventos[i].name,
                      'postid': eventos[i].id,
                      'postimage':eventos[i].images[0].url,
                      'publisher' : 'eWXNrrAlqsaW7jmjq4V3SnyU74N2',
                      'tematica': tematica,
                      'tipo': 'Público',
                      'ubicacion': ubicacion
                      }
          
                db.ref('Posts/' + eventos[i].id).set(post);
          
            }

           return res.send("Datos cargados a Firebase")
           
        })
        .catch(function (err) {
            console.log("Ocurrio un error", err);
        });



 });
