<script setup>
import {computed, defineEmits, ref} from 'vue';

const emit = defineEmits(['back-to-area', 'dates-selected']);

const startDate = ref(null);
const endDate = ref(null);

const today = new Date();
today.setHours(0, 0, 0, 0);
const currentMonth = today.getMonth();
const currentYear = today.getFullYear();
const currentDate = ref(new Date(currentYear, currentMonth, 1));

// 공휴일 목록
const holidays = {
    //2025
    '1-1': '신정',
    '1-28': '설날 연휴',
    '1-29': '설날',
    '1-30': '설날 연휴',
    '3-1': '삼일절',
    '3-3': '대체공휴일(삼일절)',
    '5-5': '어린이날, 부처님 오신날',
    '5-6': '대체공휴일(부처님 오신날)',
    '6-6': '현충일',
    '8-15': '광복절',
    '10-3': '개천절',
    '10-5': '추석 연휴',
    '10-6': '추석',
    '10-7': '추석 연휴',
    '10-8': '대체공휴일(추석)',
    '10-9': '한글날',
    '12-25': '크리스마스',
};

// 공휴일 체크 함수
const isHoliday = (day) => {
    const month = currentDate.value.getMonth() + 1;
    const dateStr = `${month}-${day}`;
    return holidays[dateStr] !== undefined;
};

// 요일 판단 함수
const getDayOfWeek = (day) => {
    const date = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), day);
    return date.getDay();
};

const daysInMonth = computed(() => {
    const year = currentDate.value.getFullYear();
    const month = currentDate.value.getMonth();
    return new Date(year, month + 1, 0).getDate();
});

const firstDayOfMonth = computed(() => {
    const year = currentDate.value.getFullYear();
    const month = currentDate.value.getMonth();
    return new Date(year, month, 1).getDay();
});

const days = computed(() => {
    const days = [];
    for (let i = 1; i <= daysInMonth.value; i++) {
        days.push(i);
    }
    return days;
});

const handleNext = () => {
    if (startDate.value) {
        emit('dates-selected', {
            start: formatDate(startDate.value),
            end: formatDate(endDate.value || startDate.value)  // endDate가 없으면 startDate로 설정
        });
    }
};

const monthYear = computed(() => {
    const options = {year: 'numeric', month: 'long'};
    return currentDate.value.toLocaleDateString('ko-KR', options);
});

const isPrevMonthDisabled = computed(() => {
    return currentDate.value.getFullYear() === currentYear &&
        currentDate.value.getMonth() === currentMonth;
});

const prevMonth = () => {
    if (!isPrevMonthDisabled.value) {
        currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1);
    }
};

const nextMonth = () => {
    currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1);
};

const selectDate = (day) => {
    const selectedDate = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), day);
    if (isPastDate(day)) {
        return;
    }
    if (!startDate.value || (startDate.value && endDate.value)) {
        startDate.value = selectedDate;
        endDate.value = null;
    } else if (selectedDate < startDate.value) {
        startDate.value = selectedDate;
    } else {
        endDate.value = selectedDate;
    }
};

const isToday = (day) => {
    const today = new Date();
    return today.getDate() === day &&
        today.getMonth() === currentDate.value.getMonth() &&
        today.getFullYear() === currentDate.value.getFullYear();
};

const isSelected = (day) => {
    const date = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), day);
    return (startDate.value && date.getTime() === startDate.value.getTime()) ||
        (endDate.value && date.getTime() === endDate.value.getTime());
};

const isInRange = (day) => {
    if (!startDate.value || !endDate.value) {
        return false;
    }
    const date = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), day);
    return date > startDate.value && date < endDate.value;
};

const formatDate = (date) => {
    if (!date) {
        return '';
    }
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const formatDisplayDate = (date) => {
    if (!date) {
        return '';
    }
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const weekday = ['일', '월', '화', '수', '목', '금', '토'][date.getDay()];
    return `${month}.${day}(${weekday})`;
};

const displayDateRange = computed(() => {
    if (startDate.value && endDate.value) {
        return `${formatDisplayDate(startDate.value)} ~ ${formatDisplayDate(endDate.value)}`;
    } else {
        return formatDisplayDate(startDate.value);
    }
});

const calculateDuration = computed(() => {
    if (startDate.value) {
        const start = new Date(startDate.value);
        const end = new Date(endDate.value || startDate.value);  // endDate가 없으면 startDate로 설정
        const diffTime = Math.abs(end - start);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        if (diffDays === 0) {
            return `당일치기`;  // 당일치기인 경우
        } else {
            return `${diffDays}박 ${diffDays + 1}일`;  // 다중일 경우
        }
    }
    return '';
});

const isPastDate = (day) => {
    const date = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), day);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return date < today;
};
</script>

<template>
    <div class="select-day-container">
        <h2>날짜 선택</h2>
        <div class="calendar">
            <div class="calendar-header">
                <button @click="prevMonth" class="nav-button" :disabled="isPrevMonthDisabled">&lt;</button>
                <h3>{{ monthYear }}</h3>
                <button @click="nextMonth">&gt;</button>
            </div>
            <div class="calendar-body">
                <div class="weekdays">
                    <span class="sunday">일</span>
                    <span>월</span>
                    <span>화</span>
                    <span>수</span>
                    <span>목</span>
                    <span>금</span>
                    <span class="saturday">토</span>
                </div>
                <div class="days">
                    <span v-for="_ in firstDayOfMonth" :key="'empty-' + _" class="empty"></span>
                    <span
                        v-for="day in days"
                        :key="day"
                        :class="{
                            'day': true,
                            'today': isToday(day),
                            'selected': isSelected(day),
                            'in-range': isInRange(day),
                            'disabled': isPastDate(day),
                            'sunday': getDayOfWeek(day) === 0,
                            'saturday': getDayOfWeek(day) === 6,
                            'holiday': isHoliday(day)
                        }"
                        @click="selectDate(day)"
                    >
                        {{ day }}
                    </span>
                </div>
            </div>
        </div>
        <div v-if="startDate" class="selected-dates">
            <div class="duration">여행 기간: {{ calculateDuration }}</div>
            <div class="date-range">{{ displayDateRange }}</div>
        </div>
        <div class="button-container">
            <button @click="handleNext" class="next-button" :disabled="!startDate">다음</button>
        </div>
    </div>
</template>

[이전 script 부분과 template 부분은 동일하게 유지]

<style scoped>
.select-day-container {
    width: 100%;
    max-width: 400px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
    background: #ffffff;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

h2 {
    text-align: center;
    margin-bottom: 24px;
    font-weight: 600;
    color: #2c3e50;
    font-size: 1.5rem;
}

.calendar {
    width: 100%;
    border: none;
    border-radius: 12px;
    padding: 16px;
    background: #ffffff;
}

.calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.calendar-header button {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
    padding: 8px 12px;
    color: #5c6ac4;
    transition: all 0.2s ease;
    border-radius: 8px;
}

.calendar-header button:hover {
    background-color: #f3f4f6;
}

.calendar-header h3 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: 600;
    color: #2c3e50;
}

.calendar-header button.nav-button:disabled {
    color: #cbd5e1;
    cursor: not-allowed;
}

.weekdays {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    text-align: center;
    font-weight: 600;
    margin-bottom: 12px;
    font-size: 0.9rem;
}

.weekdays span {
    padding: 8px;
    color: #64748b;
}

.days {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 8px;
}

.day, .empty {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 40px;
    border-radius: 10px;
    cursor: pointer;
    font-size: 0.95rem;
    transition: all 0.2s ease;
}

.day:hover:not(.disabled) {
    background-color: #f1f5f9;
}

.day.disabled {
    color: #cbd5e1 !important;
    cursor: not-allowed;
}

.today {
    color: #5c6ac4;
    font-weight: 600;
    background-color: #f3f4f6;
}

.selected {
    background-color: #5c6ac4;
    color: white !important;
    font-weight: 600;
    transform: scale(1.05);
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.3);
}

.in-range {
    background-color: #f0f3ff;
    border-radius: 6px;
}

/* 새로운 색상 스타일 */
.sunday,
.holiday {
    color: #e11d48 !important; /* 부드러운 레드 컬러 */
}

.saturday {
    color: #3b82f6 !important; /* 부드러운 블루 컬러 */
}

.selected-dates {
    margin-top: 24px;
    text-align: center;
    background: #f8fafc;
    padding: 16px;
    border-radius: 12px;
    width: 100%;
}

.duration {
    font-weight: 600;
    color: #5c6ac4;
    margin-bottom: 8px;
    font-size: 1.1rem;
}

.date-range {
    font-size: 1.1rem;
    color: #475569;
}

.button-container {
    display: flex;
    gap: 12px;
    width: 100%;
    margin-top: 24px;
}

.button-container button {
    width: 100%;
    padding: 12px 24px;
    background-color: #5c6ac4;
    color: white;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    font-size: 1rem;
    font-weight: 500;
    transition: all 0.2s ease;
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.2);
}

.button-container button:disabled {
    background-color: #cbd5e1;
    box-shadow: none;
    cursor: not-allowed;
}

.button-container button:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.3);
}

.button-container button:active:not(:disabled) {
    transform: translateY(0);
}

@media (max-width: 480px) {
    .select-day-container {
        padding: 16px;
    }

    .calendar {
        padding: 12px;
    }

    .day, .empty {
        height: 36px;
        font-size: 0.9rem;
    }

    .weekdays span {
        font-size: 0.8rem;
    }
}
</style>
