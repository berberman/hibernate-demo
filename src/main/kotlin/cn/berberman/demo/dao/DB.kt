package cn.berberman.demo.dao

import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import java.io.File

class DB private constructor() {
	companion object {
		val instance:DB by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
			DB()
		}
	}

	private lateinit var serviceRegistry: StandardServiceRegistry
	lateinit var sessionFactory: SessionFactory

	fun connect() {
		serviceRegistry = StandardServiceRegistryBuilder().configure(File("hibernate.cfg.xml")).build()
		sessionFactory = MetadataSources(serviceRegistry).buildMetadata().buildSessionFactory()
	}

	fun close() {
		sessionFactory.close()
	}
}


