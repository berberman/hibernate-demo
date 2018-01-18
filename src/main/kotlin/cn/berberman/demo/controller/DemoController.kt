package cn.berberman.demo.controller

import cn.berberman.demo.dao.transaction
import cn.berberman.demo.entity.User
import com.iyanuadelekan.kanary.core.KanaryController
import com.iyanuadelekan.kanary.helpers.http.request.done
import com.iyanuadelekan.kanary.helpers.http.response.send
import com.iyanuadelekan.kanary.helpers.http.response.withStatus
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Suppress("UNUSED_PARAMETER")
class DemoController : KanaryController() {
	fun hello(baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
		response withStatus 200 send "Hi"
		baseRequest.done()
	}

	fun addUser(baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
		request.getParameter("name").let {
			if (!it.isNullOrEmpty()) {
				transaction {
					val user = User {
						name = it
					}
					save(user)
					response withStatus HttpStatus.OK_200 send "创建用户(id:${user.id} name:${user.name})"
					true
				}
			} else
				response withStatus HttpStatus.BAD_REQUEST_400 send "请求参数错误"
			baseRequest.done()
		}
	}
	fun findAll(baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
		transaction {
//			entityManagerFactory.criteriaBuilder.createQuery()
			val c=createQuery("from User")
			val result=c.resultList
			response send result.joinToString()
			baseRequest.done()
			true
		}
	}
}