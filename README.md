# Twitter RMI: the distributed microblogging network :speech_balloon:

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

## Primera aproximación: diagrama de clases

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
