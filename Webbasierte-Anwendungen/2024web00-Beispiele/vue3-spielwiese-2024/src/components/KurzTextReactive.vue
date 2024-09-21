<template>
  <div>
    <input type="text" v-model="state.eingabe" />
    <button @click="loeschen">löschen</button>
    (noch {{ rest }} von {{max}} Zeichen)
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
const props = defineProps<{
  max: number
}>()

const state = reactive({
  eingabe: ""
})
const rest = computed(()  => props.max - state.eingabe.length)
function loeschen() {
  state.eingabe = ""
}

</script>
