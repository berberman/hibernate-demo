import org.hibernate.Session

inline operator fun <reified T> Session.get(id: Int): T = find(T::class.java, id)

fun transaction(block: TransactionBuilder.() -> Unit) {
	val session = Datasource.sessionFactory.openSession()
	val transaction = session.beginTransaction()
	TransactionBuilder(session).block()
	transaction.commit()
}
