<template>
  <td>{{ props.tour.id }}</td>
  <td>{{ props.tour.abfahrDateTime }}</td>
  <td>{{ props.tour.preis }}</td>
  <td>{{ props.tour.plaetze }}</td>
  <td>{{ props.tour.buchungen }}</td>
  <td>{{ props.tour.startOrtName }}</td>
  <td>{{ props.tour.zielOrtName }}</td>
  <td>{{ props.tour.anbieterName }}</td>
  <td>{{ props.tour.distanz }}</td>
  <td>{{ props.tour.info }}</td>
  <td><RouterLink :to="'/tour/'+props.tour.id" class="details-button">Details</RouterLink></td>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import type {ITourDTD} from "@/stores/ITourDTD";

const props = defineProps<{ tour: ITourDTD }>()
</script>

<style scoped></style>