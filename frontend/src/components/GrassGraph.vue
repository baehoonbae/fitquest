<template>
    <div>
        <h3 class="text-lg font-semibold mb-4">잔디</h3>
        <div class="grass-graph overflow-x-auto">
            <div class="min-w-[800px]">
                <div class="flex flex-col gap-1">
                    <div v-for="row in 7" :key="row" class="flex gap-1">
                        <div v-for="col in 52" :key="col" class="w-3.5 h-3.5 rounded-sm"
                            :class="getActivityColor(getActivityLevel(row, col))">
                        </div>
                    </div>
                </div>
                <div class="flex justify-between mt-2 text-sm text-gray-400">
                    <span>1월</span>
                    <span>3월</span>
                    <span>6월</span>
                    <span>9월</span>
                    <span>12월</span>
                </div>
            </div>
        </div>
        <div class="flex items-center gap-5 mt-4 justify-center">
            <div class="flex gap-1 items-center">
                <span class="text-xs text-gray-400">0</span>
                <div class="w-4 h-4 rounded-sm bg-gray-100"></div>
            </div>
            <div class="flex gap-1 items-center">
                <span class="text-xs text-gray-400">1~3</span>
                <div class="w-4 h-4 rounded-sm bg-[#c6e48b]"></div>
            </div>
            <div class="flex gap-1 items-center">
                <span class="text-xs text-gray-400">4~6</span>
                <div class="w-4 h-4 rounded-sm bg-[#7bc96f]"></div>
            </div>
            <div class="flex gap-1 items-center">
                <span class="text-xs text-gray-400">7~9</span>
                <div class="w-4 h-4 rounded-sm bg-[#239a3b]"></div>
            </div>
            <div class="flex gap-1 items-center">
                <span class="text-xs text-gray-400">10+</span>
                <div class="w-4 h-4 rounded-sm bg-[#196127]"></div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';

const getActivityLevel = (row, col) => {
    // 임의의 활동 수준을 반환 (0-3)
    return Math.floor(Math.random() * 4);
};

const getActivityColor = (level) => {
    const colors = {
        0: 'bg-gray-100',
        1: 'bg-[#c6e48b]',
        2: 'bg-[#7bc96f]',
        3: 'bg-[#239a3b]',
        4: 'bg-[#196127]'
    };
    return colors[level];
};
</script>