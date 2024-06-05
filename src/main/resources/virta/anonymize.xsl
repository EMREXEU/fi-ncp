<xsl:transform version="1.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:virta="urn:mace:funet.fi:virta/2015/09/01">

    <!-- Identity transformation -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Anonymize begin -->
    <xsl:template match="virta:Henkilotunnus/text()" priority="2">
        <xsl:text>123456</xsl:text>
    </xsl:template>

    <xsl:template match="virta:KansallinenOppijanumero/text()" priority="2">
        <xsl:text>1.2.246.562.24.11111111111</xsl:text>
    </xsl:template>

    <xsl:template match="virta:*/@opiskelijaAvain" priority="2">
        <xsl:attribute name="opiskelijaAvain">EMX000</xsl:attribute>
    </xsl:template>

    <xsl:template match="virta:Opiskeluoikeus/@avain" priority="2">
        <xsl:attribute name="avain" >EMXOO111</xsl:attribute>
    </xsl:template>

    <xsl:template match="virta:*/@opiskeluoikeusAvain" priority="2">
        <xsl:attribute name="opiskeluoikeusAvain">EMXOO111</xsl:attribute>
    </xsl:template>

    <xsl:template match="virta:*/@koulutusmoduulitunniste" priority="2">
        <xsl:attribute name="koulutusmoduulitunniste">111111TH abc</xsl:attribute>
    </xsl:template>

    <xsl:template match="virta:*/@sisaltyvaOpintosuoritusAvain/text()" priority="2">
        <xsl:text>1111111</xsl:text>
    </xsl:template>

    <xsl:template match="virta:AlkuPvm/text()" priority="2">
        <xsl:text>2014-09-02</xsl:text>
    </xsl:template>

    <xsl:template match="virta:LoppuPvm/text()" priority="2">
        <xsl:text>2014-12-31</xsl:text>
    </xsl:template>

    <xsl:template match="virta:Opintosuoritus/SuoritusPvm/text()" priority="2">
        <xsl:text>2015-04-17</xsl:text>
    </xsl:template>

    <!-- Catch all text anonymizer: this will anonymize all text nodes expect attributes  -->
    <xsl:template match="text()[normalize-space()]" priority="1">****</xsl:template>

    <!--  Catch all attributes: this will anonymize all attributes, note this will break references -->
    <xsl:template match="@*" priority="2">
        <xsl:attribute name="{name()}">
            <xsl:value-of select="generate-id(.)"/>
        </xsl:attribute>
    </xsl:template>

    <!-- Anonymize end -->
</xsl:transform>