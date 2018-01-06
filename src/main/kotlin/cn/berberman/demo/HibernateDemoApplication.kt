package cn.berberman.demo

import cn.berberman.demo.dao.Datasource
import cn.berberman.demo.dao.transaction

fun Array<String>.main() {
	Datasource.connect()
	transaction {
		println(findUser(2).name)
		deleteUser(2)
	}
	Datasource.close()
}