<template>
  <span id="starrating">
    <button @click="sternReset()"><i class="fas fa-power-off"></i></button>
    &nbsp;
    <a v-for="i in maxsterne" v-bind:key="i" v-on:click="sternGeklickt(i)">
      <i class="fas fa-star" v-bind:class="{ checked: i <= sternzahl }"></i>
    </a>
    &nbsp;
    <span class="zahlen"
      >{{ begrenztesternzahl }} / {{ maxsterne }} ({{ prozent }}%)</span
    >
    <!-- &nbsp;
    <span v-if="sternzahl === maxsterne">Super!</span>
    <span v-else-if="sternzahl <= 1">Buuh!</span>
    <span v-else>Da geht noch was</span> -->
  </span>
</template>

<script setup lang="ts">
import { computed } from "vue";

const props = withDefaults(
  defineProps<{
    maxsterne: number;
  }>(),
  { maxsterne: 5, sterne: 0 }
);

// Vue 3.3+
// meherere defineModel möglich mit Propertynamen als 1. Arg: v-model:bla="..." -> defineModel('bla', {..})
const sternzahl = defineModel<number>({ default: 0 });


// gebundene sternzahl auf 0...maxsternzahl begrenzen
const begrenztesternzahl = computed(() => Math.max(0, Math.min(sternzahl.value, props.maxsterne)))

const prozent = computed(() =>
  Math.round((begrenztesternzahl.value / props.maxsterne) * 100)
);

function sternGeklickt(i: number): void {
  if (i >= 0 && i <= props.maxsterne) {
    sternzahl.value = i;
  } 
}

function sternReset(): void {
  sternzahl.value = 0;
}
</script>

<style scoped>
a {
  color: black;
}

.zahlen {
  color: gray;
}

.checked {
  color: orange;
}
</style>
