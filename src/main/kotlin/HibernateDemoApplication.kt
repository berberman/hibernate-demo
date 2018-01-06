import entity.User

fun Array<String>.main() {
	Datasource.connect()
	transaction {
		println(findUser(2).name)
		addUser(User { name = "ppp" })
	}
	transaction {
		updateUser(findUser(3)) {
			name = "sss"
		}
	}
	Datasource.close()
}