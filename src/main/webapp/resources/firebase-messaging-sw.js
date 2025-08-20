// public/firebase-messaging-sw.js

// Firebase 호환성 버전 import (Service Worker는 compat 버전만 사용 가능)
importScripts('https://www.gstatic.com/firebasejs/9.6.11/firebase-app-compat.js')
importScripts('https://www.gstatic.com/firebasejs/9.6.11/firebase-messaging-compat.js')

// Firebase 프로젝트 설정 객체
// frontend의 firebase.js와 동일한 값이어야 함
firebase.initializeApp({
  apiKey: 'AIzaSyAx8A7u6OhgWNSUjFxTLYQqUI17frcQY9M',
  authDomain: 'fcm-test-51db7.firebaseapp.com',
  projectId: 'fcm-test-51db7',
  storageBucket: 'fcm-test-51db7.firebasestorage.app',
  messagingSenderId: '765798992395',
  appId: '1:765798992395:web:0ea90bb7d48b6d6aab550e',
  measurementId: 'G-175XSF6FBH',
})

// Firebase 메시징 객체 생성
const messaging = firebase.messaging()

// 백그라운드 또는 앱 종료 상태일 때 알림 수신
messaging.onBackgroundMessage(function (payload) {
  // 알림 제목과 본문을 payload에서 추출
  const title = payload.notification?.title || payload.data?.title
  const body = payload.notification?.body || payload.data?.body
  const url = payload.data?.url // 클릭 시 이동할 URL

  if (!title || !body) {
    console.warn('백그라운드 알림 수신 실패: 제목 또는 본문 누락')
    return
  }

  // 브라우저 알림 생성
  self.registration.showNotification(title, {
    body: body,
    data: { url: url },
    icon: '/logo.png', // 앱 아이콘 경로 (선택)
  })
})

// 알림 클릭 시 이동할 URL로 창 열기
self.addEventListener('notificationclick', function (event) {
  event.notification.close() // 알림 닫기
  const targetUrl = event.notification.data?.url || '/'

  // 해당 URL로 새 탭 또는 기존 탭 이동
  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clientList) => {
      // 기존 열린 창이 있으면 해당 창을 포커싱
      for (const client of clientList) {
        if (client.url === targetUrl && 'focus' in client) {
          return client.focus()
        }
      }
      // 없으면 새 창 열기
      if (clients.openWindow) {
        return clients.openWindow(targetUrl)
      }
    })
  )
})
