<template>
  <div>
    <div>
      Einfaches v-model führt zu String-Addition (auch wenn v-model-Refs von number sind); 1 + 1 = 11
      <br />
      <input type="text" v-model="a" />
      +
      <input type="text" v-model="b" />
      =
      {{ summeab }}
    </div>
    <div>
      Aber v-model.number konvertiert zu number. '+' nun bei number-Eingaben ok (1 + 1 = 2), bei Fehleingaben bindet er
      'falsche' Eingabe => Stringaddition wie oben (1 + 'z' = '1z')
      <br />
      <input type="text" v-model.number="c" />
      +
      <input type="text" v-model.number="d" />
      =
      {{ summecd }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";

const a = ref(0);
const b = ref(0);
const c = ref(0);
const d = ref(0);

const summeab = computed(() => a.value + b.value);
const summecd = computed(() => c.value + d.value);
</script>

<style scoped>
</style>
