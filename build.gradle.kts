import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.random.Random

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Using gradle conventions instead
}

// here are some very simple scripts that used to transform the list extracted from wikipedia,
// into the json used on the mock data.
val names by tasks.registering {
    group = "pets"
    doLast {
        val c = file("plain.txt")
            .readLines()
            .joinToString("\n") {
                it
                    .substringAfter("The ")
                    .substringBefore('(')
                    .substringBefore(' ')
                    .substringBefore(',')
                    .trim(' ', ',')

            }
        file("names.txt").writeText(c)
    }
}


val description by tasks.registering {
    group = "pets"
    doLast {
        val c = file("plain.txt")
            .readLines()
            .joinToString("\n") {
                it.substringAfter(")")
                    .trim(' ', ',', '.')
            }
        file("description.txt").writeText(c)
    }
}

val petTemplate =
    """{"id":"ID","name":"NAME","price":"PRICE","description":"DESC","type":"TYPE","dateOfBirth":"BDAY","priority":PRIORITY}"""
val petTypes = listOf(
    "DOG",
    "DOG",
    "DOG",
    "DOG",
    "DOG",
    "DOG",
    "CAT",
    "CAT",
    "CAT",
    "TURTLE",
    "TURTLE",
    "TURTLE",
    "PARROT"
)
val pets by tasks.registering {
    group = "pets"
    doLast {
        val names = file("names.txt").readLines()
        val desc = file("description.txt").readLines()
        val data = buildList(names.size) {
            repeat(names.size) { index ->
                val name = names[index]
                val description = desc[index]

                val bDay =
                    (LocalDateTime.now() - Duration.ofDays(10L + Random.nextLong(3000)))
                        .toInstant(ZoneOffset.UTC)
                        .toString()

                add(
                    petTemplate
                        .replace("ID", UUID.randomUUID().toString())
                        .replace("NAME", name)
                        .replace("PRICE", (1200 + Random.nextInt(8000)).toString())
                        .replace("DESC", description)
                        .replace("TYPE", petTypes.random())
                        .replace("BDAY", bDay)
                        .replace("PRIORITY", Random.nextFloat().toString())

                )

            }
        }.joinToString("\n")
        file("jsons.txt").writeText(data)
    }
}