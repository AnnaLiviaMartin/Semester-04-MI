<template>
  <tr class="row">
    <td>{{ item.text }}</td>
    <td>
      <StarRating :maxsterne="5" :sterne="item.stars" />
    </td>
    <td>
      <a @click="delclicked()">
        <i class="fa fa-trash"></i>
      </a>
    </td>
  </tr>
</template>

<script setup lang="ts">
import { defineEmits } from "vue";
import StarRating from "@/components/StarRating.vue";
import type { TabItem } from "./TabItem";

const props = defineProps<{
  item: TabItem
}>()

const emit = defineEmits<{
  (e:'delete-zeile', id:number): void
}>()

function delclicked(): void {
  emit("delete-zeile", props.item.id);
}
</script>
<style scoped>
</style>