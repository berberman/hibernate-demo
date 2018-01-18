package cn.berberman.demo.dao

import org.hibernate.Session

inline operator fun <reified T> Session.get(id: Int): T = find(T::class.java, id)

fun transaction(block: Session.() -> Boolean) {
	val session = Datasource.sessionFactory.openSession()
	val transaction = session.beginTransaction()
	if (session.block()) {
		transaction.commit()
		session.close()
	}

}

//class UserTransactionBuilder(private val session: Session) {
//
//
//}
