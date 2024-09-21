export interface ITourDTD {
    id: number
    abfahrDateTime: string
    preis: number
    plaetze: number
    buchungen: number
    startOrtName: string
    startOrtId: number
    zielOrtName: string
    zielOrtId: number
    anbieterName: string
    anbieterId: number
    distanz: number
    info: string
}