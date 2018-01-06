import entity.User
import org.hibernate.Session

inline operator fun <reified T> Session.get(id: Int): T = find(T::class.java, id)

fun transaction(block: UserTransactionBuilder.() -> Unit) {
	val session = Datasource.sessionFactory.openSession()
	val transaction = session.beginTransaction()
	UserTransactionBuilder(session).block()
	transaction.commit()
}
class UserTransactionBuilder(private val session: Session) {
	fun addUser(user: User) {
		session.save(user)
	}

	fun findUser(id: Int): User = session[id]

	fun updateUser(user: User, block: User.() -> Unit) = session.update(user { block() })

	fun deleteUser(id: Int) = session.delete(findUser(id))

}
