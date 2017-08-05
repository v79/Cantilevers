# Cantilevers Bridge Database

## Goals and technologies:

* HTTP server through [spark-kotlin](https://github.com/perwendel/spark-kotlin)
* Page layouts and templating with [Thymeleaf](http://www.thymeleaf.org/); others supported by spark-kotlin may follow later
* The Dependency Injection framework [Kodein](https://github.com/SalomonBrys/Kodein) - bit of learning curve
* Database handling and ORM with JetBrain's own [Exposed](https://github.com/JetBrains/Exposed), which is odd but interesting
* Reflection and annotation processing
* Unit testing with JUnit and [nhaarman's Kotlin Mockito](https://github.com/nhaarman/mockito-kotlin)
* Making it pretty through [Materializecss](https://github.com/Dogfalo/materialize)

I'm avoiding all the traditional big hitters for Java - Spring Boot, Hibernate, JSF2, JSP - as I want to learn more about alternative approaches. I'd like to use as much Kotlin as possible, but I'm not dogmatic about it. Java libraries are allowed.
