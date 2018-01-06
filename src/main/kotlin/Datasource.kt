import entity.User
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import java.io.File

object Datasource {
	lateinit var serviceRegistry: StandardServiceRegistry

	lateinit var sessionFactory: SessionFactory

	fun connect() {
		serviceRegistry = StandardServiceRegistryBuilder().configure(File("hibernate.cfg.xml")).build()
		sessionFactory = MetadataSources(serviceRegistry).buildMetadata().buildSessionFactory()
	}

	fun close() {
		sessionFactory.close()
	}
}

class TransactionBuilder(private val session: Session) {
	fun addUser(user: User) {
		session.save(user)
	}

	fun findUser(id: Int): User = session[id]

	fun updateUser(user: User, block: User.() -> Unit) = session.update(user { block() })

	fun deleteUser(id: Int) = session.delete(findUser(id))

}

