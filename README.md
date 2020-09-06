# MutantTest-MercadoLibre
Prueba MercadoLibre challenge de mutantes - Roger Sebastian Reyes Guzman

## Consideraciones			

* Se asumió que una matriz de N*N es una matriz cuadrada - en caso contrario el api produce un error 500
* El algoritmo implemenado usa Regex para realizar las busquedas, soporta la busqueda con mayusculas y minusculas pero **AAAA** y **AaAa** son mutantes y generaran registros de base de datos diferentes
* Las respuestas HTTP generan los codigos **200** y **403** según el caso pero sin body asociado
* La URI de conección a base de datos se configura en la variable de entorno **MONGO_CLUSTER_URI**.
* Para pruebas en local se puede usar una cadena de conección como esta [mongodb://root:password@localhost:27017/mercadolibre]()
* Se creó un enpoint para reiniciar los stats almacenados en la base de datos **/stats/reset-all**
	 		
## Implementación y tecnologías

* Aplicacion Spring Boot
* Maven
* MongoDB
* [Jacoco](https://www.jacoco.org/) para mediciones de covertura en los test - Plugin Maven
* AWS como host de aplicacion - Elastic Beanstalk para desplegar el api REST
* MongoRepository - Spring Data
* [MongoDB Cloud](https://www.mongodb.com/cloud) para alojar un cluster de Base de datos 1 maestro 2 replicas

## Ejecución del Api

**Url Base:** [mercadolibreexam-env.eba-4mmfbwsm.us-east-1.elasticbeanstalk.com](http://mercadolibreexam-env.eba-4mmfbwsm.us-east-1.elasticbeanstalk.com/)
	
### Operación /mutant:
**Descripción:** Servicio uilizado para detectar humanos y mutantes
**Operación:** [/mutant](http://mercadolibreexam-env.eba-4mmfbwsm.us-east-1.elasticbeanstalk.com/mutant)
**Metodo:** POST
**Headers:** Content-Type: application/json
**Body:**
```javascript
{
"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
}
```
**Respuesta:** Http 200-OK (mutante) y Http 403-Forbidden (Humano)

### Operación /stats:
**Descripción:** Servicio uilizado para consultar los stats de ejecuciones previas
**Operación:** [/stats](http://mercadolibreexam-env.eba-4mmfbwsm.us-east-1.elasticbeanstalk.com/stats)
**Metodo:** GET
**Headers:** Content-Type: application/json
**Respuesta:** Http 200-OK (mutante)

### Operación /reset:
**Descripción:** Servicio uilizado reiniciar los stats de la Base de Datos
**Operación:** [/stats/reset-all](http://mercadolibreexam-env.eba-4mmfbwsm.us-east-1.elasticbeanstalk.com/stats/reset-all)
**Metodo:** DELETE
**Headers:** Content-Type: application/json
**Respuesta:** Http 204-NO CONTENT
					
## Ejecución Local

* Clonar el repositorio del proyecto [MutantTest-MercadoLibre.git](https://github.com/rogerreyes/MutantTest-MercadoLibre.git)
* El proyecto necesita tener configurada la variable de entorno **MONGO_CLUSTER_URI** con la direccion del cluster suministrada en el correo, en caso de ejecutar el proyecto con una base Mongo en local, basta con usar una cadena de conección como esta **"mongodb://root:password@localhost:27017/mercadolibre"**, remplazando el usuario y el password de la configuración local.
* **mvn install** generará la carpeta **/targer** y en su interior el archivo "examen-0.0.1-SNAPSHOT.jar"
* Ejecutar con el comando **java -jar examen-0.0.1-SNAPSHOT.jar**
* Los test unitarios apuntan a una base de datos independiente en cluster de mongo en la nube y no se necesita que este configurada localmente

## Reportes de Cobertura
* **mvn install** ejecuta el plugin de **JACOCO** y genera en el directorio **/target** el path  **site/jacoco** con los reportes de Cobertura ejecutar el archivo **index.html** para ver una vista Web de los reportes