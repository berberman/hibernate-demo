package cn.berberman.demo

import cn.berberman.demo.controller.DemoController
import cn.berberman.demo.dao.DB
import com.iyanuadelekan.kanary.app.KanaryApp
import com.iyanuadelekan.kanary.core.KanaryRouter
import com.iyanuadelekan.kanary.handlers.AppHandler
import com.iyanuadelekan.kanary.middleware.simpleConsoleRequestLogger
import com.iyanuadelekan.kanary.server.Server

fun Array<String>.main() {
	DB.instance.connect()
	server().listen(2333)
	DB.instance.close()
}

fun server() = Server().apply {
	handler = AppHandler(KanaryApp().apply app@ {
		DemoController().let {
			KanaryRouter().apply router@ {
				on("user/") use it
				post("addUser/", it::addUser)
				get("hello/", it::hello)
				get("all/", it::findAll)
				this@app.apply {
					mount(this@router)
					use(simpleConsoleRequestLogger)
				}
			}
		}
	})
}