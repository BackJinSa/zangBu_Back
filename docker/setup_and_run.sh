#!/usr/bin/env bash
set -euo pipefail

DOMAIN="api.zangbu.site"
EMAIL="saranghein@gmail.com"
PROJECT_DIR="/root/zangbu"

CERT_BASE="/etc/letsencrypt"
LIVE_DIR="$CERT_BASE/live/$DOMAIN"
ARCHIVE_DIR="$CERT_BASE/archive/$DOMAIN"
RENEWAL_CONF="$CERT_BASE/renewal/$DOMAIN.conf"
FULLCHAIN="$LIVE_DIR/fullchain.pem"
PRIVKEY="$LIVE_DIR/privkey.pem"
WEBROOT="$PROJECT_DIR/certbot/www"
LOG="$CERT_BASE/letsencrypt.log"

echo "== Zangbu setup =="
echo "DOMAIN: $DOMAIN"
echo "EMAIL : $EMAIL"
echo

mkdir -p "$WEBROOT/.well-known/acme-challenge"

cd "$PROJECT_DIR"

echo "[1/8] 이미지 빌드"
docker compose build

echo "[2/8] 컨테이너 기동"
docker compose up -d

# --- 유틸: LE 인증서인지 판별 ---
is_le_cert=false
if [[ -s "$FULLCHAIN" ]]; then
  if openssl x509 -in "$FULLCHAIN" -noout -issuer 2>/dev/null | grep -qi "Let's Encrypt"; then
    is_le_cert=true
  fi
fi

echo "[3/8] ACME webroot / 프록시 사전 점검"
echo ok > "$WEBROOT/.well-known/acme-challenge/ping" || true
HDRS="$(curl -sI "http://$DOMAIN/.well-known/acme-challenge/ping" || true)"
if echo "$HDRS" | grep -qi "Server: cloudflare"; then
  echo " - 경고: Cloudflare 프록시가 아직 ON으로 보입니다(회색=DNS Only 로 변경 필요)."
fi
if echo "$HDRS" | grep -q " 200 " || echo "$HDRS" | grep -q " 301 "; then
  echo " - ACME webroot 외부 접근 OK"
else
  echo " - 경고: ACME webroot 외부 접근 실패(80/보안그룹/프록시 확인). 그래도 진행합니다."
fi

echo "[4/8] 기존 인증서 상태 점검"
if [[ "$is_le_cert" == "true" ]]; then
  echo " - 이미 Let's Encrypt 인증서가 존재합니다: $LIVE_DIR"
else
  # live/renewal 메타가 있으나 LE가 아니면(=self-signed/중복) 백업
  if [[ -d "$LIVE_DIR" || -f "$RENEWAL_CONF" || -d "$ARCHIVE_DIR" ]]; then
    TS="$(date +%Y%m%d-%H%M%S)"
    echo " - 기존 live/renewal 메타 백업: .$TS"
    [[ -d "$LIVE_DIR"     ]] && mv "$LIVE_DIR"     "${LIVE_DIR}.bak.$TS"
    [[ -d "$ARCHIVE_DIR"  ]] && mv "$ARCHIVE_DIR"  "${ARCHIVE_DIR}.bak.$TS"
    [[ -f "$RENEWAL_CONF" ]] && mv "$RENEWAL_CONF" "${RENEWAL_CONF}.bak.$TS"
  fi

  echo "[5/8] Certbot 발급(webroot, non-interactive, 강제)"
  docker run --rm \
    -v "$CERT_BASE:$CERT_BASE" \
    -v "$WEBROOT:/var/www/certbot" \
    certbot/certbot certonly \
    --non-interactive \
    --webroot -w /var/www/certbot \
    -d "$DOMAIN" \
    --agree-tos -m "$EMAIL" --no-eff-email \
    --force-renewal --cert-name "$DOMAIN" || {
      echo " - 실패: 자세한 로그 → $LOG"
      echo "   * 80/tcp 개방, Cloudflare 프록시(회색), DNS A레코드 재확인 후 재시도하세요."
      exit 1
    }

  echo "[6/8] Nginx 재시작(실 인증서 적용)"
  docker compose restart nginx
fi

echo "[7/8] 인증서 적용 확인"
if openssl x509 -in "$FULLCHAIN" -noout -issuer 2>/dev/null | grep -qi "Let's Encrypt"; then
  echo " - ✅ Let's Encrypt 인증서 적용됨"
else
  echo " - ⚠️ 여전히 LE가 아님(아마 self-signed)."
  echo "   * curl -I http://$DOMAIN/.well-known/acme-challenge/ping → 200/301?"
  echo "   * docker compose logs --tail=120 nginx"
  echo "   * Cloudflare는 발급 시 회색(프록시 OFF)인지 확인"
fi

# 갱신 크론
CRON_FILE="/etc/cron.d/certbot-docker"
CRON_LINE='0 4,16 * * * root cd '"$PROJECT_DIR"' && docker run --rm -v '"$CERT_BASE"':'"$CERT_BASE"' -v '"$WEBROOT"':/var/www/certbot certbot/certbot renew && docker compose exec nginx nginx -s reload'
if [[ ! -f "$CRON_FILE" ]]; then
  echo "[8/8] 갱신 크론 등록"
  echo "$CRON_LINE" > "$CRON_FILE"
  chmod 644 "$CRON_FILE"
  systemctl restart cron || true
else
  echo "[8/8] 갱신 크론 이미 존재: $CRON_FILE"
fi

echo
echo "완료! https://$DOMAIN 로 확인해보세요."
