#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="/root/zangbu"
COMPOSE="docker compose"

usage() {
  cat <<'EOF'
배포 스크립트

사용법:
  ./deploy.sh stop                # 모든 컨테이너 중지/삭제 (볼륨 유지)
  ./deploy.sh up                  # 컨테이너 기동
  ./deploy.sh rebuild             # 캐시 없이 이미지 재빌드 (변경사항 확실 반영)
  ./deploy.sh redeploy            # 멈춤 → 캐시없이 빌드 → 기동 (가장 흔한 재배포)
  ./deploy.sh restart             # 컨테이너 재시작
  ./deploy.sh logs                # 톰캣 로그 팔로우
  ./deploy.sh status              # 상태 확인
  ./deploy.sh reset-all           # ⚠️ 볼륨까지 모두 삭제(완전 초기화)

팁:
- 새 WAR 업로드 후 'redeploy'를 쓰면 안전합니다.
- reset-all 은 Redis 데이터/인증서 등 모든 볼륨이 삭제됩니다. 주의!
EOF
}

cd "$PROJECT_DIR"

cmd="${1:-help}"

case "$cmd" in
  stop)
    echo "[STOP] containers down (volumes kept)"
    $COMPOSE down
    ;;
  up)
    echo "[UP] start containers"
    $COMPOSE up -d
    ;;
  rebuild)
    echo "[REBUILD] build images with --no-cache"
    $COMPOSE build --no-cache
    ;;
  redeploy)
    echo "[REDEPLOY] stopping containers..."
    $COMPOSE down
    echo "[REDEPLOY] building images (no cache)..."
    $COMPOSE build --no-cache
    echo "[REDEPLOY] starting containers..."
    $COMPOSE up -d
    echo "[REDEPLOY] showing tomcat health & logs (Ctrl+C to exit)"
    $COMPOSE ps
    echo
    $COMPOSE logs -f tomcat
    ;;
  restart)
    echo "[RESTART] restarting containers"
    $COMPOSE restart
    ;;
  logs)
    echo "[LOGS] tail -f tomcat"
    $COMPOSE logs -f tomcat
    ;;
  status)
    echo "[STATUS]"
    $COMPOSE ps
    ;;
  reset-all)
    echo "⚠️ 정말로 볼륨까지 전부 삭제할까요? (이 작업은 되돌릴 수 없습니다)"
    read -rp "타이핑: YES 를 입력하면 진행합니다: " ans
    if [[ "${ans:-}" == "YES" ]]; then
      echo "[RESET] down -v (remove volumes)"
      $COMPOSE down -v
      echo "[RESET] images rebuild (no cache)"
      $COMPOSE build --no-cache
      echo "[RESET] up"
      $COMPOSE up -d
      $COMPOSE ps
    else
      echo "취소했습니다."
      exit 1
    fi
    ;;
  help|*)
    usage
    ;;
esac
