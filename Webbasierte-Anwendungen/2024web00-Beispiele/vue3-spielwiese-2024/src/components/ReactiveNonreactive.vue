<template>
  <div>
    <div>
      zahl {{ zahl }}, zahlref {{ zahlref }}, robj.a {{ robj.a }}, a {{ a }}<br />
      malzehn {{ malzehn }}, malzehncomp {{ malzehncomp }}<br />
      <button v-on:click="aktion()">Aktion!</button>
    </div>

  </div>
</template>

<script setup lang="ts">
import { computed, ref, reactive } from "vue";

let zahl = 1; // zahl nicht reaktiv
const zahlref = ref(zahl);  // zahlref reaktiv
const robj = reactive({ a: 10, b: 20 })  // reaktiv

function aktion() {
  zahl++
  zahlref.value *= 10
  robj.a += 5
}

const a = robj.a // a nicht reaktiv
const malzehn = 10 * zahlref.value // malzehn nicht reaktiv
const malzehncomp = computed(() => 10 * zahlref.value) // reaktiv

</script>

<style scoped>
</style>
