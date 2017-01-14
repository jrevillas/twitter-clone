# Twitter RMI: the distributed microblogging network :speech_balloon:

## Introducción y objetivos

El desarrollo de esta práctica consiste en crear una réplica de la red social Twitter haciendo uso de Java RMI. No todas las características de Twitter deben implementarse, solamente el registro y autenticación de usuarios, la publicación de nuevos estados o _tweets_, generación de la cronología, la completa gestión de los mensajes privados y las correspondientes notificaciones mediante _callbacks_ de RMI.

## Metodología de trabajo

Es obligatorio dirigir el desarrollo de la práctica mediante una metodología ágil. En el repositorio de la práctica se pueden consultar al detalle las tareas que han ido surgiendo a lo largo de las reuniones de seguimiento. Se han intentado rotar los roles respecto a la práctica anterior, procurando que todos los componentes del grupo aportaran su visión a la arquitectura de comunicación basada en RMI.

- [x] Base de datos: esquema y primeras consultas (#1 @j.rcalle)
- [x] Preparar despliegue en Amazon Lightsail (#2 @j.revillas)
- [x] Estructura básica del cliente y servidor RMI (#3 @daniel.mchaves y @j.revillas)
- [x] Modelado de la interfaz con Java Swing (#4 @m.nunezd)
- [x] Nueva sección para conocer usuarios (#5 @j.rcalle y @j.revillas)
- [x] Mensajes significativos en el lado del servidor (#6 @j.rcalle)
- [x] Cliente web con Angular 2, Java Spark y WebSockets de HTML5 (#7 @j.revillas)
- [x] Soporte para biografías de los usuarios (#8 @j.rcalle y @j.revillas)
- [ ] Identificar y manejar distintos tipos de notificaciones (#9 @j.revillas)
- [x] Despliegue en Amazon Lightsail y Amazon S3 (#10 @j.rcalle y @j.revillas)
- [x] Soporte para mensajes privados (#11 @j.rcalle y @j.revillas)
- [x] Bug: problemas al recibir los mensajes privados entrantes (#12 @j.rcalle)
- [ ] Documentación y memoria del proyecto (#13 @j.revillas)
- [x] Bug: permitir lectura de ficheros en la política de seguridad (#14 @daniel.mchaves)
- [ ] Soporte para mensajes privados en el cliente Java (#15 @m.nunezd)
- [x] Almacenar las contraseñas cifradas con bcrypt (#16 @j.rcalle)
- [ ] Directorio o servicio de avatares (#17)
- [ ] Compilación automatizada en CI y pruebas adicionales (#18)
- [ ] Tratamiento de las notificaciones en el cliente Java (#19 @m.nunezd)

## Primera aproximación a la arquitectura

De acuerdo a los ejemplos vistos en la asignatura, se ha desarrollado un modelo basado en interfaces (comunes entre cliente y servidor) y clases que implementan dichas interfaces. Salvo los _callbacks_, todas las clases se implementan en el lado del servidor. Los ejemplos más significativos son las clases [`Twitter`](#), [`User`](#) y [`Status`](#).

La implementación de la clase [`Twitter`](#) se expone en el registro de tal manera que cualquier cliente pueda obtener una instancia de dicha clase. Una vez obtenida, se pueden invocar dos métodos: `login(...)` y `register(...)`. Ambos devuelven referencias a objetos [`User`](#), cuyos métodos se describen en la interfaz común. En caso de error devolverán `null`.

Si el usuario es capaz de obtener una referencia a un objeto del tipo [`User`](#), este podrá invocar todos los métodos propios de un usuario autenticado. Con este procedimiento se resuelve el problema de acceso a métodos no permitidos. De una manera similar a la expuesta anteriormente, los usuarios podrán recibir objetos (o colecciones de objetos) del tipo [`Status`](#). Un ejemplo de ello es el método `getTimeline()`, disponible a través de la interfaz [`User`](#).

## Capa de persistencia en el servidor

El modelo de datos ha sido uno de los puntos más importantes desde el principio del desarrollo. Se decide utilizar Redis por cuestiones de velocidad, ya que mantiene los datos en memoria y permite almacenar estructuras de datos completas (_hashes_ y listas). El controlador para Java es muy sencillo y eficiente, lo que también hizo sumar puntos contra MySQL y otros gestores relacionales. La instalación de Redis se realiza mediante un contenedor Docker:

```bash
FROM ubuntu:14.04
RUN apt-get update && apt-get install -y redis-server
EXPOSE 6379
ENTRYPOINT ["/usr/bin/redis-server"]
```

Los datos relativos a los usuarios se almacenan en forma de _hashes_, incluyendo el nombre, la contraseña cifrada con bcrypt, la fecha de ingreso, la dirección URL del avatar, etc. Para obtener los datos de un usuario bastaría con ejecutar la consulta `HGETALL jrevillas:profile`.

Las cronologías y los seguidores se han implementado en forma de listas. Cada usuario tiene asociada una lista de seguidores y otra con los usuarios a los que sigue. Consultar estas listas es tan sencillo como ejecutar `LRANGE jrevillas:followers 0 -1` o `LRANGE jrevillas:following 0 -1`. Con la finalidad de hacerlo más significativo, se dicidieron utilizar los propios identificadores de los usuarios como referencias (en lugar de un contador incremental).

Para los _tweets_ sí se ha hecho uso de identificadores numéricos: `HGETALL 842:status`. El hecho de almacenar las cronologías de cada usuario en listas separadas ha permitido manejar mejores tiempos de carga en el servidor.

## Interfaz gráfica con Java Swing

La interfaz gráfica está realizada con _Java Swing_. Esta interfaz está dividida en dos ventanas, una ventana inicial para el login y otra ventana para el panel principal de _Twitter RMI_.

El panel principal de _Twitter RMI_ esta compuesto de cinco partes: en la parte superior están los botones de acciones (_Timeline_, _Privates Messages_ y _New Tweet_), en la parte central se mostran los _tweets_ que forman el _timeline_ de cada usuario o los mensajes privados que hayamos enviado o recibido, en la parte superior derecha se muestra la información del perfil del usuario (nombre de usuario, biografía y avatar), en la parte central derecha se muestra un listado de todos aquellos usuarios a los que seguimos y en la parte inferior derecha se encuentra un botón que sirve para poder buscar a otros usuarios dentro de _Twitter RMI_.

![alt text](http://imgur.com/3nn8zYI.png "Login interface")

![alt text](http://imgur.com/8tPYl7O.png "Timeline interface")

![alt text](http://imgur.com/ET71Wxg.png "Tweet interface")

![alt text](http://imgur.com/gcI8fz0.png "View private message interface")

![alt text](http://imgur.com/0N5p7GH.png "Write private message interface")


## Interfaz web: arquitectura y comunicación

Se ha desarrollado una aplicación web utilizando Angular 2 y la librería de WebSockets de HTML5 para interactuar con un servidor web que a su vez será cliente RMI de la aplicación principal.

El servidor HTTP dispone de los endpoints necesarios que invocan los métodos remotos correspondientes en la capa de RMI, lo que quiere decir que los endpoints HTTP van a "envolver" los métodos de las clases Twitter, User y Status. La aplicación web y el servidor con el que se comunica abstraen los problemas adicionales ocasionados por la autenticación (manejo de tokens).

Todos los endpoints se han construido sobre los métodos GET y POST de HTTP e intentan representar la información en formato JSON manteniendo siempre la mayor similitud con las clases Java que representan esos recursos. No se ha seguido el estándar de arquitectura RESTful.

En las siguientes imágenes se muestra la apariencia de la aplicación web.

![alt text](http://imgur.com/23hWpNw.png "Web interface")

![alt text](http://imgur.com/uTb9CFI.png "Web interface")

![alt text](http://imgur.com/hIwuABs.png "Web interface")

## Notificaciones: callbacks y WebSockets

Las notificaciones que genera _Twitter RMI_, para avisar a los usuarios de eventos que están sucediendo dentro de la aplicación están implementadas mediante _callbacks_. Dichos _callbacks_ serán tratados de diferente manera según la interfaz de usuario que se este utilizando.

Para la gestión de notificaciones en la interfaz gráfica con _Java Swing_, se ha optado por hacerlo mediante notificaciones de tipo _Toast_. Los _callbacks_ que se reciban mediante la clase _ClientCallbackImpl.java_ serán tratados mediante la clase _ToastMessage_, que hará visible al usuario las notificaciones con el aspecto que se puede ver en la imagen adjunta.

![alt text](http://imgur.com/HW2vnSZ.png "Notification Interface")

Para la gestión de notificaciones en la aplicación web, se ha añadido un servicio con WebSockets, que se inicializará incluyendo autenticación cuando el usuario haga login a través del endpoint correspondiente. El servidor HTTP (también cliente RMI) recibe notificaciones por parte del servidor de objetos remotos para todos los usuarios que estén utilizando el servicio web. El servidor HTTP los recibe por RMI y los envia a cada cliente web utilizando este WebSocket. Este es uno de los puntos donde ambas arquitecturas coinciden: el callback de un cliente que accede por la web desencadena un mensaje por el WebSocket que mantiene con el cliente para poder notificar. Las notificaciones por el WebSocket se reflejan tal y como se muestra en la imagen.

Hemos notado problemas al desplegar el servicio web completo en Amazon Lightsail, ya que las conexiones mediante WebSocket se reiniciaban a los pocos minutos. Esto se resolvía al probar con una Raspberry Pi, pero no tenía suficiente capacidad para alojar la arquitectura completa del servidor.

Las notificaciones se muestran en una capa superior utilizando la opción z-index de CSS3, como se muestra en la imagen adjunta.

![alt text](http://imgur.com/nMBfWeE.png "Web Notificacions")

## Despliegue de las aplicaciones en Amazon Lightsail

Para la realización de la práctica, se ha optado por tener todos los servicios necesarios alojados en uno de los servidores de _Amazon Lightsail_ y tenerlo asociado a un dominio registrado en OVH (`twitter-rmi.com`). La máquina virtual (EC2) tiene alojado el servidor de objetos RMI, la base de datos Redis y el proceso Java que utiliza Spark como servidor HTTP (es el mismo para la capa HTTP y los WebSockets). El frontend, realizado con Angular 2, se aloja en Amazon S3 para obtener mejores tiempos de carga.

## Problemas encontrados

Al procurar un despliegue completo, hemos encontrado bastantes problemas relacionados con la parte de red. El único que no hemos llegado a arreglar era el ocasionado por los callbacks: resultaba imposible notificar a un usuario mediante callbacks, aunque funcionara el resto de la aplicación. Este problema no aparece en la aplicación web, ya que el stack HTTP y el servidor de objetos RMI se encuentran en la misma máquina.

Por lo demás, ha sido una práctica fácil de depurar. La experiencia de haber trabajado previamente con JMS facilita bastante el trabajo y los ejemplos vistos en clase han sido muy gráficos.