package com.example.controller.user

import com.example.lib.database.query
import com.example.model.user.User
import com.example.model.user.UserClass
import com.example.model.user.UserTable
import org.jetbrains.exposed.sql.select


object UserController {
    suspend fun getLogin(_user: User.Login) = query {
        UserTable.slice(UserTable.id, UserTable.password)
            .select { UserTable.email eq _user.email}
            .map { User.of(it) }.firstOrNull()
    }

    suspend fun postUser(_user: User) = query {
        UserTable.slice(UserTable.email)
            .select { UserTable.email eq _user.email }
            .empty().apply {
                if (this) UserClass.new {
                    name = _user.name
                    phone_number = _user.phone_number
                    email = _user.email
                    password = _user.password
                }
            }
    }

}




/**
Field : 필드, 항목, 어떠한 의미를 지니는 정보의 한 조각으로, 데이터베이스
시스템에서 처리의 최소 단위가 되는 것.

Table : 테이블, 빠른 참조를 위해 적당한 형태로 자료를 모아 놓은 것. 관계
데이터 베이스 모델(relational data base model)에서 자료의 구조를 2차원의
표로 나타낸 것. 즉, 행과 열의 형태로 관리되며 키를 지정함으로써 원하는
자료를 빠르고 쉽게 찾아 낼 수도 있다.

Row(행) : 관계형 데이터베이스에서 레코드(record) 또는 튜플(tuple)로불리기도
하며, 어떤 테이블에서 단일 구조 데이터 항목을 가리킨다.
간단한 용어로, 데이터베이스 테이블은 로우와 컬럼 또는 필드로 구성되어 있다고
간주할 수 있다. 각 테이블의 행은 일련의 관련 자료를 나타내며,
테이블에서 모든 로우는 동일한 구조를 가지고 있다.

Column(열) : 관계형데이터베이스 테이블에서 특정한 단순 자료형의
일련의 데이터값과 테이블에서의 각 열을 말한다.
컬럼은 열이 어떻게 구성되어야 할 지에 대한 구조를 제공한다.
관계형 데이터베이스 용어에서 컬럼과 같은 의미로 사용되는 것은
속성(attribute)이다.

필드(field)가 종종 컬럼의 대용으로 동일한 의미로 사용되지만,
필드와 필드값은 한 열이나 한 컬럼 사이의 교차로 존재하는 단일 항목을
특정할 때 언급하는 것이다.


엔터티: 데이터 베이스의 개념 중에서도 데이터 모델에 대해 공부를 시작할 때
제일 먼저 나오는 개념이 '엔터티(Entity)' 이다.
엔터티는 쉽게 말해 실체, 객체라고 생각할 수 있다.

 */
//
//
//        /**
//         * DB의 CREATE, ALTER, DROP 의 테이블 단위 조작은
//         * 쿼리문으로 하지않고 직접 객체를 수정하거나 테이블 단위로 수정한다.
//         * 그러나 테이블 객체를 만들고 나중에 추가하면 안되는 걸로
//         * 혹은 디비버와 같은 db 프레임워크 이용 수동으로 관리
//         */
//         // val _columns: MutableList<Column<*>> = table.columns 다른것들도 다 읽기전용
//         //table.registerColumn("이름", VarCharColumnType( 30))
//        //table.foreignKey(password)
//        /**
//         * DB의 주요 기능 insert, update, delete, select
//         * insert: 세로운 레코드 추가
//         * update: 레코드 내용 수정
//         * delete: 레코드 삭제
//         * select: 레코드를 선택
//         */
//
//        /**
//         * DB의 인서트, DAO의 new, CRUD의 create, http의 post
//         */
//        try{
//           Auths.insert {
//                it[email] = "The Last Jedi12321312"
//                it[password] = "1235"
//                it[isLogin] = false
//            }
//        }catch (e: Exception){}
//        try{
//            val list = listOf<AuthInput>(
//                AuthInput("11a1aacc311ff13aa1a","123123",isLogin = false),
//                AuthInput("11aa3cc2113ff1aa","123123",isLogin = false),
//                AuthInput("11aa1cc331affa444414","123123",isLogin = false)
//            )
//            Auths.batchInsert(list){
//                this[Auths.email] = it.email
//                this[Auths.password] =it.password
//                this[Auths.isLogin] =it.isLogin
//            }
//        }catch (e: Exception){ }
//
//        try{
//            //테이블 객체와 다르게 서버단에서 에러 호출
//            //트라이문 안에있어도
////            entityClass.new {
////                email = "email11"
////                this.password = "password"
////                this.isLogin = false
////            }
//
//        }catch (e: Exception){}
//        /**
//         * DB의 셀렉트, DAO의 all, find, CRUD의 create, http의 post
//         *
//         *  옵션 이용 ( 옵션은 구분 해도되지만 람다 형식으로 불리언값 반환해야함)
//         */
//
//
//        val query = Auths.selectAll().forEach { println(1) }
//        val query2 = Auths.select{email eq auth.email}.forEach { println(1) }
//        val query3 = Auths.selectBatched{
//            email eq auth.email
//            password eq "1234"
//        }.forEach { println(1) }
//
//
//        Auth.all()
//        Auth.find{email eq auth.email}
//        Auth.findById(10)
//
//        /**
//         * DB의 업데이트, DAO는 props 수정 , CRUD의 create, http의 post
//         */
//
//        Auths.update({ Auths.id eq 1 }){
//            it[Auths.email] = "change Email!"
//            println(it)
//        }
//
//        Auth.findById(1)?.email = "haha" ;
//        /**
//         * DB의 딜리트, DAO는 props 수정 , CRUD의 create, http의 post
//         */
//
//        Auths.deleteWhere { Auths.id greater 80 }
//        Auths.deleteIgnoreWhere { Auths.id greater 70 }
//
////            Auth.find{Auths.id greater 50}.forEach { it.delete() }
//        val condition = when {
//            auth.email != null && auth.password != null
//                -> Op.build { Auths.email eq auth.email and (Auths.id greater 1) }
//            auth.email != null
//                -> Op.build { Auths.id greater 10 }
//            auth.password != null
//                -> Op.build { Auths.id lessEq 50 }
//            else
//                -> null
//        }
//        condition?.let { Auths.select(condition).forEach { println() }} ?: Auths.selectAll().forEach { println() }
//
//        val query = Auths.selectAll()
//        auth.password.let{
//            query.andWhere { Auths.password eq it }
//        }
////        query.forEach { println() }
//
//        auth.email.let{
//            query.adjustColumnSet { innerJoin(Users,{Auths.email}, {Users.email}) }
//                .adjustSlice { slice(fields + Users.columns) }
//                    .andWhere { email eq it }
//        }
//        query.forEach { println() }


//        [com.example.controller.Auth@2beb0647, com.example.controller.Auth@2902c6fa,
//        com.example.controller.Auth@1514866f, com.example.controller.Auth@56829f36,
//        com.example.controller.Auth@25a8629f, com.example.controller.Auth@1a2245c6,
//        com.example.controller.Auth@2bb48f13, com.example.controller.Auth@690dde7c,
//        com.example.controller.Auth@fd7e558, com.example.controller.Auth@7538d11,
//        com.example.controller.Auth@81056ff, com.example.controller.Auth@f6987bf]
//셀렉트문은 끝나고 리절트로우를 반환한다.
//        val query: Query =  Auths// Fs 반환
//            .selectAll()//필드셋을 쿼리로 넘겨주면서 쿼리 반환
//            .withDistinct()
//        val resultRow = Auth.wrapRows(query).toList()
//        val passwordFromResultRow = resultRow.map { resultRow -> resultRow.password }
//        println(passwordFromResultRow)
//        val password: List<String> = Auths.slice(Auths.password)
//            .select{Auths.email eq auth.email}.withDistinct().map { resultRow: ResultRow -> resultRow[Auths.password]  }
//
//        val count = Auths.select { Auths.id eq  8 }.count()
//        val count2 = Auths.select { Auths.id eq  8 }.limit(2, offset = 1)
//        println(count2)
//
//        (Boards innerJoin Users)
//            .slice(Boards.firstName.count(),  Users.firstName)
//            .select{ Users.lastName eq Boards.lastName }
//           .groupBy(Users.firstName)
//
//package com.example.controller
//
//import com.example.lib.database.query
//import com.example.router.UserInput
//import org.jetbrains.exposed.dao.IntEntity
//import org.jetbrains.exposed.dao.IntEntityClass
//import org.jetbrains.exposed.dao.id.EntityID
//import org.jetbrains.exposed.dao.id.IntIdTable
//
//
//object Users : IntIdTable("Users", "userId"){z
//    val email = reference("email", Auths)
//    val password =  reference("password", Auths)
//    val firstName = varchar("firstName", 30)
//    val lastName = varchar("lastName", 30)
//}
//
//class User(accountId: EntityID<Int>): IntEntity(accountId){
//    companion object : IntEntityClass<User>(Users)
//    var email by Auth referencedOn Users.email
//    var password by Auth referencedOn Users.password
//    var firstName by Users.firstName
//    var lastName by Users.lastName
//}
//
//class UserController {
//    suspend fun postUser(userInput: UserInput) = query {
//        if( Auth.find { Auths.email eq userInput.email }.empty()){
//            var auth = Auth.new {
//                email = userInput.email
//                password = userInput.password
//                isLogin = false
//            }
//
//            User.new {
//                email = auth
//                password = auth
//                firstName = userInput.firstName
//                lastName = userInput.lastName
//            }
//            return@query true
//        }else{
//            return@query false
//        }
//    }
//}

