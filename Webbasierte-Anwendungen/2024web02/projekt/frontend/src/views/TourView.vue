<template>
  <div v-if="tour">
    <div v-if="tour.distanz > 300 && info !=''">
    </div>
  </div>

  <div class="tour-header">
    <h1>Tour: {{ tour?.id }}: {{ tour?.startOrtName }} - {{ tour?.zielOrtName }}</h1>
    <p>{{ tour?.info }}</p>
  </div>
  <div class="tour-details">
    <span><strong>Abfahrt am:</strong> {{ tour?.abfahrDateTime }}</span>
    <span><strong>Preis:</strong> {{ tour?.preis }} EUR</span>
    <span><strong>Anbieter:</strong> {{ tour?.anbieterName }}</span>
    <span><strong>Plätze:</strong> Es sind von {{ tour?.plaetze }} Plätzen bisher {{ tour?.buchungen }} gebucht ({{ freiePlaetze }} freie Plätze)</span>
  </div>

</template>

<script setup lang="ts">
import {computed, onMounted, ref, watchEffect} from "vue";
import {useTourenStore} from "@/stores/tourenstore";
import { useInfo } from '@/composables/useInfo'

const {info, loescheInfo, setzeInfo} = useInfo()
const props = defineProps<{ tourid: string }>()
//const {liste, okbool, tourdata, updateTourListe} = useTourenStore()
const { tourdata, updateTourListe } = useTourenStore()

watchEffect(async () => {
  console.log("onMounted")
  if (!tourdata.ok) {
    await updateTourListe();
  }
  if (tour.value && tour.value.distanz > 300) {
    loescheInfo()
    setzeInfo("Die Tour ist länger als 300km!")
  } else {
    loescheInfo()
  }

});

const tour = computed(() => {return tourdata.lst.find(ele => ele.id == parseInt(props.tourid))})
const freiePlaetze = computed(() => tour.value ? tour.value.plaetze - tour.value.buchungen : 0)

</script>

<style scoped>
.tour-header {
  padding: 10px;
  margin-bottom: 10px;
}

.tour-details span {
  display: block;
  margin-bottom: 5px;
  padding: 10px;
}
</style>