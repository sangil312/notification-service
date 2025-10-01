package com.dev.notificationapp.domain.notification.infrastructure

import com.dev.notificationapp.domain.notification.Notification
import com.dev.notificationapp.domain.notification.QNotification.notification
import com.dev.notificationapp.domain.notification.NotificationCustomRepository
import com.dev.notificationapp.domain.notification.enums.OrderCondition
import com.dev.notificationapp.domain.notification.enums.OrderCondition.*
import com.querydsl.core.types.Order.*
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class NotificationRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : NotificationCustomRepository {

    override fun findAllByUserId(userId: Long, pageable: Pageable): Page<Notification> {

        val content = queryFactory
            .selectFrom(notification)
            .where(notification.user.id.eq(userId))
            .orderBy(pageable.toSingleOrderCondition())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = queryFactory
            .select(notification.count())
            .from(notification)
            .where(notification.user.id.eq(userId))

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    private fun Pageable.toSingleOrderCondition(): OrderSpecifier<*> {
        val order = this.sort.firstOrNull { requestOrder -> OrderCondition.existsByCondition(requestOrder.property) }
            ?: return OrderSpecifier(DESC, notification.createdAt)

        val direction = if (order.isAscending) ASC else DESC
        return when (order.property) {
            CREATED_AT.value -> OrderSpecifier(direction, notification.createdAt)
            RESERVED_AT.value -> OrderSpecifier(direction, notification.reservedAt)
            else -> OrderSpecifier(DESC, notification.createdAt)
        }
    }
}