<template>
  <div>
    <input type="text" v-model="eingabe" />
    <button @click="loeschen">löschen</button>
    (noch {{ rest }} von {{max}} Zeichen)
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
const props = defineProps<{
    max: number
}>()

const eingabe = ref("");

const rest = computed(()  => props.max - eingabe.value.length)

function loeschen() {
  eingabe.value = ""
}
</script>
