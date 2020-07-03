const functions = require('firebase-functions');
const rp = require('request-promise');

var admin = require("firebase-admin");

var serviceAccount = require("./service_key.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://forevents-2c0e8.firebaseio.com"
});

let db = admin.database();

exports.obtenerEventFul = functions.https.onRequest((request, res) => {
    var options = {
        uri: 'http://api.eventful.com/json/events/search?app_key=QkgvCnHdvDTbx9KZ&location=Espa%C3%B1a&date=Future&include=categories&sort_order=date&change_multi_day_start=true&page_size=200&page_number=1',
        json: true // Automatically parses the JSON string in the response
    };
     
    rp(options).then(function (repos) {
            let eventos = repos.events.event;
            console.log(eventos[0].title);
            for(var i = 0; i<eventos.length; i++){

                var title = eventos[i].title;
                var url = eventos[i].url;
              
                if(eventos[i].description){
                      var descripcion = eventos[i].description +" Para mas información consulte en el siguiente enlace: "+ url;
              
                      descripcion = descripcion.replace(/<[^>]*>?/g, " ");
              
                }else{
                        descripcion = "Sin determinar";
                }
              
                if(eventos[i].stop_time){
                  var fin = eventos[i].stop_time;
                  var arrayFin =  fin.split(" ");
                  var fechaFin = arrayFin[0]; 
                  var horaFin = arrayFin[1];
                   horaFin = horaFin.split(":");
                  var hFin = horaFin[0] +":"+ horaFin[1];
                }else{
                    fechaFin = "Sin determinar";
                   hFin = "00:00";
                }
              
                var inicio = eventos[i].start_time;
                var arrayInicio =  inicio.split(" ");
                var fechaInicio = arrayInicio[0]; 
                var horaInicio = arrayInicio[1];
                 horaInicio = horaInicio.split(":");
                var hInicio = horaInicio[0] +":"+ horaInicio[1];
              
                var id = eventos[i].id;
              
                var imagen = "";
                var tematica= "";
              
                  if(eventos[i].categories){
                    var categoria = eventos[i].categories.category[0].id;
              
              if(categoria === "music"){
                              imagen = "https://image.freepik.com/foto-gratis/personas-festival_1160-736.jpg";
                              tematica = "Ocio";
                          }else if (categoria === "conference"){
                              imagen = "https://image.freepik.com/foto-gratis/vista-posterior-audiencia-escuchando-conferencia_41418-3202.jpg";
                              tematica = "Social";
                          }else if (categoria === "comedy"){
                              imagen = "https://image.freepik.com/foto-gratis/artista-masculino-femenino-mime-que-mira-escondidas-cortina-roja_23-2147891612.jpg";
                               tematica = "Ocio";
                          }else if (categoria === "learning_education"){
                              imagen = "https://image.freepik.com/foto-gratis/concepto-educacion-estudiante-estudiar-brainstorming-campus-concepto-cerca-estudiantes-que-discuten-su-tema-libros-o-libros-texto-enfoque-selectivo_1418-627.jpg";
                              tematia = "Cultural";
                          }else if (categoria === "family_fun_kids"){
                              imagen = "https://image.freepik.com/foto-gratis/ninos-jugando-sobre-cesped_1098-504.jpg";
                              tematica = "Social";
                          }else if (categoria === "festivals_parades"){
                              imagen = "https://image.freepik.com/foto-gratis/amigos-saltando-calles-parque-atracciones_23-2148281641.jpg";
                                 tematica = "Ocio";
                          }else if (categoria === "movies_film"){
                              imagen = "https://image.freepik.com/foto-gratis/claqueta-cerca-sabrosas-palomitas-maiz_23-2147803977.jpg";
                                  tematica = "Ocio";
                          }else if (categoria === "food"){
                              imagen = "https://image.freepik.com/foto-gratis/arreglo-plano-hamburguesas-pizza_23-2148308817.jpg";
                                  tematica = "Social";
                          }else if (categoria === "fundraisers"){
                              imagen = "https://image.freepik.com/foto-gratis/grupo-voluntarios-felices-diversos_53876-15120.jpg";
                                  tematica = "Comunitario";
                          }else if (categoria === "art"){
                              imagen = "https://image.freepik.com/foto-gratis/pincel-manchado-pintura_23-2148002444.jpg";
                                 tematica = "Cultural";
                          }else if (categoria === "support"){
                              imagen = "https://image.freepik.com/foto-gratis/jovenes-empresarios-juntando-sus-manos_23-2148187231.jpg";
                                 tematica = "Social";
                          }else if (categoria === "holiday"){
                              imagen = "https://image.freepik.com/foto-gratis/vista-superior-elementos-esenciales-viaje-equipaje-espacio-copia_23-2148434436.jpg";
                                 tematica = "Cultural";
                          }else if (categoria === "books"){
                              imagen = "https://image.freepik.com/foto-gratis/libro-biblioteca-libro-texto-abierto_1150-5918.jpg";
                                 tematica = "Cultural";
                          }else if (categoria === "attractions"){
                              imagen = "https://image.freepik.com/foto-gratis/rueda-maravilla-cabanas-gente-cielo_23-2148328039.jpg";
                                 tematica = "Ocio";
                          }else if (categoria === "community"){
                              imagen = "https://image.freepik.com/vector-gratis/vecinos-balcones_23-2148539929.jpg";
                                 tematica = "Comunitario";
                          }else if (categoria === "business"){
                              imagen = "https://image.freepik.com/foto-gratis/concepto-hombres-negocios-apreton-manos_53876-31214.jpg";
                                 tematica = "Empresarial";
                          }else if (categoria === "singles_social"){
                              imagen = "https://image.freepik.com/foto-gratis/bailarina-energica-luz-glamour-dinamico_1098-4307.jpg";
                                 tematica = "Ocio";
                          }else if (categoria === "schools_alumni"){
                              imagen = "https://image.freepik.com/foto-gratis/ninos-salon-clase-levantando-manos_23-2147659015.jpg";
                                 tematica = "Acádemico";
                          }else if (categoria === "clubs_associations"){
                              imagen = "https://image.freepik.com/foto-gratis/grupo-inicio-positivo-computadoras-portatiles-chateando-sala-reuniones_74855-3651.jpg";
                                 tematica = "Social";
                          }else if (categoria === "outdoors_recreation"){
                              imagen = "https://image.freepik.com/foto-gratis/relajese-aventura-estilo-vida-caminando-concepto-idea-viaje_1421-652.jpg";
                                 tematica = "Social";
                          }else if (categoria === "performing_arts"){
                              imagen = "https://image.freepik.com/foto-gratis/mujer-violin_144627-26178.jpg";
                                 tematica = "Cultural";
                          }else if (categoria === "animals"){
                              imagen = "https://image.freepik.com/foto-gratis/lindo-perro-gato-amigo_23-2148332378.jpg";
                                 tematica = "Social";
                          }else if (categoria === "politics_activism"){
                              imagen = "https://image.freepik.com/foto-gratis/escala-justicia-oro-frente-libro-lectura-abogado-mesa_23-2147898544.jpg";
                                 tematica = "Politico";
                          }else if (categoria === "sales"){
                              imagen = "https://image.freepik.com/foto-gratis/tablero-madera-tabla-vacia-fondo-borroso-perspectiva-madera-marron-sobre-desenfoque-almacen-grande-puede-utilizar-exhibicion-o-montaje-su-products-mock-arriba-exhibicion-producto_1253-1066.jpg";
                                 tematica = "Social";
                          }else if (categoria === "science"){
                              imagen = "https://image.freepik.com/foto-gratis/examen-muestra-microscopio_1098-18424.jpg";
                                 tematica = "Cultural";
                          }else if (categoria === "religion_spirituality"){
                              imagen = "https://image.freepik.com/foto-gratis/pequenas-velas-llamas_23-2147785816.jpg";
                                 tematica = "Comunitario";
                          }else if (categoria === "sports"){
                              imagen = "https://image.freepik.com/vector-gratis/fondo-patrones-deportes-fisuras_98292-4294.jpg";
                                 tematica = "Ocio";
                          }else if (categoria === "technology"){
                              imagen = "https://image.freepik.com/vector-gratis/concepto-ecologia-tecnologica-holograma_23-2148443645.jpg";
                                 tematica = "Cultural";
                          }else if (categoria === "other"){
                              imagen = "https://image.freepik.com/foto-gratis/microfono-negro-sala-conferencias_1232-3128.jpg";
                              tematica = "Social";
                          }else{
                           imagen = "https://image.freepik.com/foto-gratis/microfono-negro-sala-conferencias_1232-3128.jpg";
                           tematica = "Social";
                          }
              
                  }else{
                        categoria = "Sin determinar";
                        imagen = "https://image.freepik.com/foto-gratis/microfono-negro-sala-conferencias_1232-3128.jpg";
                        tematica = "Social";
                  }
              
                  var direccion = eventos[i].venue_name;
              
                  if(eventos[i].venue_address){
                    direccion = direccion +", "+eventos[i].venue_address;
                  }
              
                  if(eventos[i].city_name){
                    direccion = direccion+", "+eventos[i].city_name;
                  }
              
                  if(eventos[i].region_name){
                    direccion = direccion+", "+eventos[i].region_name;
                  }
              
                  if(eventos[i].country_name === "Spain"){
                    direccion = direccion+", "+ "España";
                  }else{
                    direccion = direccion+", "+eventos[i].country_name;
                  }
              
              
                      var post = {
                          'aforo': 'Sin determinar',
                          'descripcion': descripcion,
                          'fechaFin': fechaFin,
                          'fechaInicio' : fechaInicio,
                          'horaFin': hFin,
                          'horaInicio' : hInicio,
                          'nombre': title,
                          'postid': id,
                          'postimage':imagen,
                          'publisher' : 'MH9jqhjZszNSZtepkRKZNQjd3Of1',
                          'tematica': tematica,
                          'tipo': 'Público',
                          'ubicacion': direccion
                          }
              
                          db.ref('Posts/' + id).set(post);

                    }

                    return res.send("Datos cargados a Firebase")


        })
        .catch(function (err) {
            console.log("Ocurrio un error",err)
        });

});