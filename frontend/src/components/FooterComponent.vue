<script setup>
// 외부 링크이므로 script setup은 비워둡니다
</script>

<template>
    <div class="footer-container">
        <div class="footer-content">
            <div class="footer-info">
                <div class="footer-links">
                    <a href="https://policies.google.com/terms?hl=ko"
                       target="_blank"
                       rel="noopener noreferrer"
                       class="footer-link">Google 이용약관</a>
                    <a href="https://policies.google.com/privacy?hl=ko"
                       target="_blank"
                       rel="noopener noreferrer"
                       class="footer-link">Google 개인정보처리방침</a>
                </div>
            </div>
        </div>
        <div class="footer-bottom">
            <div class="footer-bottom-content">
                <span>&copy; {{ new Date().getFullYear() }} Trip For P</span>
                <span class="divider">|</span>
                <a href="mailto:contact@tripforp.co.kr">contact@tripforp.co.kr</a>
            </div>
        </div>
    </div>
</template>

<style scoped>
.footer-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: end;
    padding: 20px 0;
    color: #6E6F73;
    border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.footer-content {
    width: 60%;
    max-width: 1200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
}

.footer-info {
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 100%;
}

.footer-info p {
    margin: 5px 0;
    color: #1a1a1a;
    font-weight: 500;
}

.footer-links {
    display: flex;
    gap: 15px;
    justify-content: center;
    width: 100%;
}

.footer-link {
    color: #4a5568;
    text-decoration: none;
    font-size: 0.9em;
    padding: 5px 10px;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.footer-link:hover {
    background-color: rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
}

.divider {
    padding: 0 5px;
}

.footer-bottom {
    width: 100%;
    text-align: center;
    padding: 15px 0 5px 0;
}

.footer-bottom-content {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 5px;
    flex-wrap: wrap;
}

.footer-bottom span,
.footer-bottom a {
    font-size: 0.9em;
    color: #4a5568;
    margin: 0;
}

@media (max-width: 1024px) {
    .footer-content {
        width: 80%;
    }
}

@media (max-width: 768px) {
    .footer-content {
        width: 90%;
        padding: 0 15px;
    }

    .footer-link {
        font-size: 0.85em;
        padding: 8px;
    }
}

@media (max-width: 480px) {
    .footer-content {
        padding: 0 10px;
    }

    .footer-links {
        flex-direction: column;
        gap: 8px;
    }

    .footer-link {
        width: 100%;
        padding: 10px 0;
        font-size: 0.8em;
    }

    .divider {
        display: none;
    }

    .footer-bottom-content {
        flex-direction: column;
        gap: 8px;
    }

    .footer-info p {
        font-size: 0.8em;
    }

    .footer-bottom span,
    .footer-bottom a {
        font-size: 0.8em;
    }
}
</style>
