<template>
  <div class="rahmen">
      Eingabe:
      <input type="text" v-model="eingabe" />
      <button v-on:click="loeschen">Löschen</button>
    <p>Umrechnung mit Faktor {{ faktor }} ergibt: {{ ergebnis }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";

const props = defineProps({
  faktor: { type: Number, required: true, default: 17 },
})

const eingabe = ref(0);
const ergebnis = computed(() => eingabe.value * props.faktor);

function loeschen() {
  eingabe.value = 0
}
</script>

<style scoped>
  .rahmen {
    border: 2px solid gray;
    padding: 2px;
    margin: 2px;
  }
</style>
