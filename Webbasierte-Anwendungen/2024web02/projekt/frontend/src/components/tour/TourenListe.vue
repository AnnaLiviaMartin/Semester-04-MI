<template>
  <table class="list-style">
    <thead>
    <tr>
      <th>ID</th>
      <th>Abfahrt</th>
      <th>Preis</th>
      <th>Plätze</th>
      <th>Buchungen</th>
      <th>Startort</th>
      <th>Zielort</th>
      <th>Anbieter</th>
      <th>Distanz</th>
      <th>Info</th>
      <th>Details</th>
    </tr>
    </thead>
    <tbody>
      <tr v-for="tour in props.touren" :key="tour.id">
        <TourListeZeile :tour="tour"/>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import TourListeZeile from './TourListeZeile.vue'
import type {ITourDTD} from "@/stores/ITourDTD";

const props = defineProps<{ touren: Array<ITourDTD> }>()
</script>

<style scoped></style>