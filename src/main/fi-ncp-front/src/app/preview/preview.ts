export interface ArvosanaAsteikkoMuuTyyppi {
  asteikko: {
    nimi: string
    asteikkoArvosana: AsteikkoArvosana[]
    avain: string
  }
  koodi: string
}

export interface AsteikkoArvosana {
  koodi: string
  nimi: string
  laskennallinenArvo: number
  avain: string
}
