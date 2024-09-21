<template>
  <div class="mainContent">
    <h1 class="title">Das aktuelle Mitfahrangebot</h1>

    <div id="suchfeld">
      <input v-model="suchfeld" placeholder="Suche nach Start- oder Zielort" />
      <button class="details-button" @click="resetSearch">Reset</button>
    </div>

    <TourenListe :touren="filteredTouren"></TourenListe>
  </div>
</template>

<script setup lang="ts">

import TourenListe from '@/components/tour/TourenListe.vue'
import { useTourenStore } from '@/stores/tourenstore'
import { onMounted } from 'vue'
import { computed, ref } from 'vue'

// const { liste, updateTourListe } = toRefs(store) mit .value() drauf zugreifen, dann meckert aber Zeile 10
const store = useTourenStore()
onMounted(async () => {
 await store.updateTourListe()
})

// Suchstring Ref
const suchfeld = ref('')

// Computed property, um die Touren basierend auf dem Suchstring zu filtern
const filteredTouren = computed(() => {
  const n: number = suchfeld.value.length
  if (n < 1) {
    return store.liste
  } else {
    return store.liste.filter(tour =>
      tour.startOrtName.toLowerCase().includes(suchfeld.value.toLowerCase()) ||
      tour.zielOrtName.toLowerCase().includes(suchfeld.value.toLowerCase()))
  }
})

// Methode, um den Suchstring zurückzusetzen
function resetSearch() {
  suchfeld.value = ''
}

</script>

<style scoped></style>
