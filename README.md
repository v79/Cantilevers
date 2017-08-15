# _Cantilevers_ Bridge Database

In a truer life I would have been a civil engineer. If I had the math, the draughtsmanship, the foresight, and an understanding of trigonometry beyond SOH-CAH-TOA, and I’d have liked to have built bridges for a living. Sadly, none of these things are true. I still like bridges, though. The best of them are elegant, inspired, beautiful, substantive, whimsical, and yet practical, useful and pragmatic.
This experimental project will present a database of bridges, drawing on information from WikiData and hopefully other sources in the future. The user will be able to browse and search for bridges, add new entries via WikiData, and ultimately feed updates back to WikiData.

The design and requirements are a little fuzzy, and I doubt this will ever be truly useful. But I’m hoping it will be fun to write. Commentary on the project can be found [on my blog](http://www.liamjdavison.co.uk/).

## Goals and technologies:

* HTTP server through [spark-kotlin](https://github.com/perwendel/spark-kotlin)
* Page layouts and templating with [Thymeleaf](http://www.thymeleaf.org/); others supported by spark-kotlin may follow later
* The Dependency Injection framework [Kodein](https://github.com/SalomonBrys/Kodein)
* Database handling and ORM with JetBrain's own [Exposed](https://github.com/JetBrains/Exposed)
* Unit testing with JUnit and [nhaarman's Kotlin Mockito](https://github.com/nhaarman/mockito-kotlin)
* Making it pretty through [Materializecss](https://github.com/Dogfalo/materialize)

I'm avoiding all the traditional big hitters for Java - Spring Boot, Hibernate, JSF2, JSP.
