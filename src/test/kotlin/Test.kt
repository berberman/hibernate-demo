import cn.berberman.demo.dao.DB
import cn.berberman.demo.dao.condition
import cn.berberman.demo.dao.expression
import cn.berberman.demo.dao.session
import cn.berberman.demo.entity.User
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class Test {
	@Before
	fun init() {
		DB.instance.connect()
	}

	@After
	fun close() {
		DB.instance.close()
	}

	@Test
	fun test() {
		session {
			transaction {
				query<User> {
					fromThis() where condition(
							User::name to expression {
								not() like "aaa"
							},
							User::id to expression {
								this between 2..10
							})
				}
				query<User> {
					fromThis() where condition(
							User::name to expression {
								not() `in` listOf("aaa", "bbb")
							},
							User::id to expression {
								this equals "2"
							})
				}
			}
		}
	}
}