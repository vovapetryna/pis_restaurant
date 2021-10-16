import java.security.SecureRandom

import de.mkammerer.argon2.Argon2Factory.Argon2Types
import de.mkammerer.argon2._

package object utils {

  object passwordHashing {
    private lazy val argon2: Argon2 = Argon2Factory.create(Argon2Types.ARGON2d)

    def hashPassword(password: String, salt: String): String =
      argon2.hash(2, 16383, 4, (salt + password).toCharArray)

    def verifyPassword(hash: String, password: String, salt: String): Boolean =
      argon2.verify(hash, (salt + password).toCharArray)
  }

  object salt {
    private val secureRandom = new SecureRandom()
    def generate(length: Int = 16, bound: Int = 93, range: Int = 33): String = {
      val sb = new StringBuffer()
      for (_ <- 1 to length) {
        sb.append((secureRandom.nextInt(bound) + range).toChar)
      }
      sb.toString
    }
  }

}
