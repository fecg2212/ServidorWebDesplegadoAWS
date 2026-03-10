# Mini Web Framework en Java desplegado en AWS EC2 - Felipe Calvache

## Descripción

En este proyecto se desarrolló un **mini framework web en Java** capaz de manejar solicitudes HTTP y exponer servicios REST mediante anotaciones personalizadas como `@RestController`, `@GetMapping` y `@RequestParam`.

El framework utiliza **reflexión de Java** para detectar automáticamente los controladores y registrar los endpoints disponibles.

Posteriormente, el servidor fue **compilado y desplegado en una instancia EC2 de AWS**, permitiendo que los servicios sean accesibles desde internet mediante la apertura del puerto correspondiente en el **Security Group**.

---

## Arquitectura General

El sistema funciona de la siguiente manera:

1. El usuario realiza una solicitud HTTP desde el navegador.
2. La solicitud llega a la instancia EC2 en AWS.
3. El servidor Java procesa la petición.
4. El framework identifica el endpoint correspondiente mediante anotaciones.
5. Se ejecuta el método del controlador y se retorna la respuesta al cliente.

```
Usuario (Navegador)
        │
        │ HTTP Request
        ▼
Internet
        │
        ▼
AWS EC2 Instance
        │
        ▼
Servidor Web Java
        │
        ▼
Framework → Controllers → Response
```

---

## Estructura del Proyecto

```
TDSE-ServidorWebDesplegado
│
├── deployment
│   ├── classes.zip
│   └── myKey.pem
│
├── microframework
│   │
│   ├── imagenes
│   │
│   ├── src
│   │   └── main
│   │       └── java
│   │           └── co
│   │               └── edu
│   │                   └── escuelaing
│   │                       ├── annotations
│   │                       │   ├── GetMapping.java
│   │                       │   ├── RequestParam.java
│   │                       │   └── RestController.java
│   │                       │
│   │                       ├── controllers
│   │                       │   ├── GreetingController.java
│   │                       │   └── HelloController.java
│   │                       │
│   │                       ├── server
│   │                       │   └── HttpServer.java
│   │                       │
│   │                       └── MicroSpringBoot.java
│   │
│   ├── target
│   ├── .gitignore
│   └── pom.xml
│
└── README.md
```

---

## Funcionalidades implementadas

- Servidor HTTP básico en Java.
- Framework basado en **anotaciones personalizadas**.
- Registro automático de endpoints usando **reflexión**.
- Soporte para:
  - `@RestController`
  - `@GetMapping`
  - `@RequestParam`
- Respuesta de servicios REST en formato texto/HTML.
- Despliegue del servidor en **AWS EC2**.

---

## Ejemplo de Endpoint

Ejemplo de controlador implementado:

```java
@RestController
public class GreetingController {

    @GetMapping("/greeting")
    public String greeting(
        @RequestParam(value = "name", defaultValue = "World") String name) {
        return "<h1>Hola, " + name + "!</h1>";
    }
}
```

Ejemplo de uso desde el navegador:

```
http://ec2-@IP_PUBLICA.compute-1.amazonaws.com:8080/greeting?name=Felipe
```

Respuesta:

```
Hola Felipe
```

---

## Despliegue en AWS EC2

El servidor fue desplegado en una instancia EC2 siguiendo los siguientes pasos:

1. Compilación del proyecto con Maven.

```
mvn clean compile
```

2. Compresión de las clases generadas.

```
zip -r classes.zip classes
```

3. Transferencia del archivo al servidor mediante **SFTP**.

```
sftp -i myKey.pem ec2-user@ec2-@IP_PUBLICA.compute-1.amazonaws.com
sftp> put classes.zip
```

4. Conexión a la instancia mediante **SSH**.

```
ssh -i myKey.pem ec2-user@ec2-@IP_PUBLICA.compute-1.amazonaws.com
```

5. Instalación de Java en la instancia.

```
sudo yum install java-21-amazon-corretto-devel
```

6. Ejecución del servidor.

```
java -cp classes co.edu.escuelaing.MicroSpringBoot
```

7. Apertura del puerto correspondiente en el **Security Group** para permitir acceso externo.

---

## Evidencias

### Compilación del proyecto

![alt text](/microframework/imagenes/image-4.png)

---

### Transferencia de archivos a EC2 y configuración de puertos

![alt text](/microframework/imagenes/image-6.png)

![alt text](/microframework/imagenes/image-5.png)
---

### Ejecución del servidor en EC2

![alt text](/microframework/imagenes/image-7.png)

---

### Acceso al servicio desde el navegador

![alt text](/microframework/imagenes/image-8.png)

![alt text](/microframework/imagenes/image-9.png)

![alt text](/microframework/imagenes/image-10.png)

---

## Conclusiones

- Se logró implementar un **mini framework web en Java** utilizando anotaciones y reflexión.
- Se comprendió el funcionamiento básico de un **servidor HTTP y el manejo de endpoints REST**.
- Se realizó el **despliegue del servidor en la nube usando AWS EC2**, permitiendo el acceso público al servicio.
- Este proyecto demuestra conceptos fundamentales utilizados por frameworks modernos como **Spring Boot**.