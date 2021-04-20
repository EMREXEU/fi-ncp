<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml" standalone="no" omit-xml-declaration="no"/>
    <xsl:template match="elmo">
        <fo:root language="EN">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4-portrail" page-height="297mm" page-width="210mm" margin-top="5mm" margin-bottom="5mm" margin-left="5mm" margin-right="5mm">
                    <fo:region-body margin-top="25mm" margin-bottom="20mm"/>
                    <fo:region-before region-name="xsl-region-before" extent="25mm" display-align="before" precedence="true"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A4-portrail">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:table table-layout="fixed" width="100%" font-size="10pt" border-color="black" border-width="0.4mm" border-style="solid">
                        <fo:table-column column-width="proportional-column-width(20)"/>
                        <fo:table-column column-width="proportional-column-width(45)"/>
                        <fo:table-column column-width="proportional-column-width(20)"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Generated:
                                        <xsl:call-template name="formatdate">
                                            <xsl:with-param name="DateTimeStr" select="generatedDate"/>
                                        </xsl:call-template>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block font-size="150%">
                                        Emrex - Finnish National Contact Point
                                    </fo:block>
                                    <fo:block space-before="3mm"/>
                                </fo:table-cell>
                                <fo:table-cell text-align="right" display-align="center" padding-right="2mm">
                                    <fo:block display-align="before" space-before="6mm">Page <fo:page-number/> of <fo:page-number-citation ref-id="end-of-document"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body" border-collapse="collapse" reference-orientation="0">
                    <fo:block>ELMO REPORT</fo:block>
                    <fo:table table-layout="fixed" width="100%" font-size="10pt" border-color="black" border-width="0.35mm" border-style="solid" text-align="left" display-align="before" space-after="5mm">
                        <fo:table-column column-width="proportional-column-width(18)"/>
                        <fo:table-column column-width="proportional-column-width(32)"/>
                        <fo:table-column column-width="proportional-column-width(18)"/>
                        <fo:table-column column-width="proportional-column-width(32)"/>
                        <fo:table-body font-size="95%">
                            <fo:table-row>
                                <fo:table-cell font-weight="bold" padding="6px">
                                    <fo:block font-size="12pt" padding-bottom="4px">Learner:</fo:block>
                                    <fo:block padding-bottom="1px">Given names:</fo:block>
                                    <fo:block padding-bottom="1px">Family name:</fo:block>
                                    <fo:block padding-bottom="1px">Date of Birth:</fo:block>
                                    <fo:block padding-bottom="1px">Gender:</fo:block>
                                    <xsl:for-each select="learner/identifier">
                                        <fo:block padding-bottom="1px"><xsl:value-of select="@type"/>:</fo:block>
                                    </xsl:for-each>
                                </fo:table-cell>
                                <fo:table-cell padding="6px">
                                    <fo:block font-size="12pt" font-weight="bold" padding-bottom="4px">&#x00A0;</fo:block>
                                    <fo:block padding-bottom="1px"><xsl:value-of select="learner/givenNames"/></fo:block>
                                    <fo:block padding-bottom="1px"><xsl:value-of select="learner/familyName"/></fo:block>
                                    <fo:block padding-bottom="1px">
                                        <xsl:call-template name="formatdate">
                                            <xsl:with-param name="DateTimeStr" select="learner/bday"/>
                                        </xsl:call-template>
                                    </fo:block>
                                    <fo:block padding-bottom="1px">
                                        <xsl:call-template name="formatGender">
                                            <xsl:with-param name="isoGender" select="learner/gender"/>
                                        </xsl:call-template>
                                    </fo:block>
                                    <fo:block padding-bottom="1px" font-weight="bold"> </fo:block>
                                    <xsl:for-each select="learner/identifier">
                                        <fo:block padding-bottom="1px">
                                            <xsl:value-of select="."/>
                                        </fo:block>
                                    </xsl:for-each>
                                </fo:table-cell>
                                <fo:table-cell font-weight="bold" padding="6px">
                                    <fo:block font-size="12pt" padding-bottom="4px">Issuer:</fo:block>
                                    <fo:block>Country:</fo:block>
                                    <xsl:for-each select="report/issuer/title">
                                        <fo:block>
                                            Title (<xsl:value-of select="@xml:lang"/>):
                                        </fo:block>
                                    </xsl:for-each>
                                    <xsl:for-each select="report/issuer/identifier">
                                        <fo:block>
                                            Identifier (<xsl:value-of select="@type"/>):
                                        </fo:block>
                                    </xsl:for-each>
                                    <fo:block>URL:</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="6px">
                                    <fo:block font-size="12pt" font-weight="bold" padding-bottom="4px">&#x00A0;</fo:block>
                                    <fo:block><xsl:value-of select="report/issuer/country"/></fo:block>
                                    <xsl:for-each select="report/issuer/title">
                                        <fo:block>
                                            <xsl:value-of select="."/>
                                        </fo:block>
                                    </xsl:for-each>
                                    <xsl:for-each select="report/issuer/identifier">
                                        <fo:block>
                                            <xsl:value-of select="."/>
                                        </fo:block>
                                    </xsl:for-each>
                                    <fo:block><xsl:value-of select="report/issuer/url"/></fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                    <fo:table table-layout="fixed" width="100%" font-size="10pt" text-align="left" display-align="before" space-after="5mm">
                        <fo:table-column column-width="proportional-column-width(100)"/>
                        <fo:table-body font-size="95%">
                            <xsl:for-each select="report/learningOpportunitySpecification">
                                <fo:table-row>
                                    <fo:table-cell padding="6px">
                                        <fo:block padding-bottom="1px">
                                            <xsl:call-template name="los">
                                                <xsl:with-param name="los" select="."/>
                                            </xsl:call-template>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                    <fo:block id="end-of-document"></fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template name="formatdate">
        <xsl:param name="DateTimeStr" />

        <xsl:variable name="mm">
            <xsl:value-of select="substring($DateTimeStr,6,2)" />
        </xsl:variable>

        <xsl:variable name="dd">
            <xsl:value-of select="substring($DateTimeStr,9,2)" />
        </xsl:variable>

        <xsl:variable name="yyyy">
            <xsl:value-of select="substring($DateTimeStr,1,4)" />
        </xsl:variable>

        <xsl:value-of select="concat($yyyy,'-', $mm, '-', $dd)" />
    </xsl:template>

    <xsl:template name="formatGender">
        <xsl:param name="isoGender" />

        <xsl:variable name="gender">
        <xsl:choose>
            <xsl:when test="$isoGender = '1'">
                <xsl:value-of select="'Male'"/>
            </xsl:when>
            <xsl:when test="$isoGender = '2'">
                <xsl:value-of select="'Female'"/>
            </xsl:when>
            <xsl:when test="$isoGender = '9'">
                <xsl:value-of select="'Not applicable'"/>
            </xsl:when>
            <xsl:when test="$isoGender = '0'">
                <xsl:value-of select="'Not known'"/>
            </xsl:when>
        </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$gender" />
    </xsl:template>

    <xsl:template name="los">
        <xsl:param name="los" />
            <fo:block font-weight="bold" font-size="12pt" padding-bottom="4px">Learning Opportunity Specification:</fo:block>
            <xsl:for-each select="$los/title">
                <fo:block>
                    <fo:inline font-weight="bold">
                        Title (<xsl:value-of select="@xml:lang"/>):
                    </fo:inline>
                    <xsl:value-of select="."/>
                </fo:block>
            </xsl:for-each>
            <xsl:for-each select="$los/identifier">
                <fo:block>
                    <fo:inline font-weight="bold">
                        Identifier (<xsl:value-of select="@type"/>):
                    </fo:inline>
                    <xsl:value-of select="."/>
                </fo:block>
            </xsl:for-each>
            <fo:block>
                <fo:inline font-weight="bold">Type: </fo:inline>
                <xsl:value-of select="$los/type" />
            </fo:block>
            <xsl:variable name="isced" select="$los/iscedCode" />
            <xsl:choose>
                <xsl:when test="$isced != ''">
                    <fo:block>
                        <fo:inline font-weight="bold">
                            ISCED code:&#x00A0;
                        </fo:inline>
                        <xsl:value-of select="$isced"/>
                    </fo:block>
                </xsl:when>
            </xsl:choose>
            <xsl:variable name="subject" select="$los/subjectArea" />
            <xsl:choose>
                <xsl:when test="$subject != ''">
                    <fo:block>
                        <fo:inline font-weight="bold">
                            Subject area:&#x00A0;
                        </fo:inline>
                        <xsl:value-of select="$subject"/>
                    </fo:block>
                </xsl:when>
            </xsl:choose>
            <fo:block>
                <fo:block font-weight="bold">Specifies:</fo:block>
                <fo:block margin-left="8mm">
                    <xsl:call-template name="loi">
                        <xsl:with-param name="loi" select="$los/specifies/learningOpportunityInstance"/>
                    </xsl:call-template>
                </fo:block>
            </fo:block>
            <xsl:choose>
            <xsl:when test="$los/hasPart">
                <fo:block>
                    <fo:inline font-weight="bold">
                        Has part:
                    </fo:inline>
                </fo:block>
            </xsl:when>
          </xsl:choose>
            <xsl:for-each select="$los/hasPart">
                <fo:block margin-left="16mm">
                    <xsl:call-template name="los">
                        <xsl:with-param name="los" select="learningOpportunitySpecification"/>
                    </xsl:call-template>
                </fo:block>
            </xsl:for-each>
    </xsl:template>

    <xsl:template name="loi">
        <xsl:param name="loi" />
        <fo:block font-weight="bold" font-size="12pt" padding-bottom="4px">Learning Opportunity Instance:</fo:block>
            <xsl:for-each select="$loi/identifier">
                <fo:block>
                    <fo:inline font-weight="bold">
                        Identifier (<xsl:value-of select="@type"/>):
                    </fo:inline>
                    <xsl:value-of select="."/>
                </fo:block>
            </xsl:for-each>
            <fo:block>
                <fo:inline font-weight="bold">Status: </fo:inline>
                <xsl:value-of select="$loi/status" />
            </fo:block>
            <xsl:variable name="result" select="$loi/resultLabel" />
            <xsl:choose>
                <xsl:when test="$result != ''">
                    <fo:block>
                        <fo:inline font-weight="bold">
                            Result:&#x00A0;
                        </fo:inline>
                        <xsl:value-of select="$result"/>
                    </fo:block>
                </xsl:when>
            </xsl:choose>
            <fo:block>
                <fo:inline font-weight="bold">Registration date: </fo:inline>
                <xsl:call-template name="formatdate">
                    <xsl:with-param name="DateTimeStr" select="$loi/date"/>
                </xsl:call-template>
            </fo:block>
             <xsl:for-each select="$loi/credit">
                <fo:block>
                    <fo:inline font-weight="bold">
                        Credits (<xsl:value-of select="scheme"/>):
                    </fo:inline>
                    <xsl:value-of select="value"/>
                </fo:block>
            </xsl:for-each>
            <fo:block>
                <fo:inline font-weight="bold">Level: </fo:inline>
                <xsl:value-of select="$loi/level/type" />
            </fo:block>
            <fo:block>
                <fo:inline font-weight="bold">Language of instruction: </fo:inline>
                <xsl:value-of select="$loi/languageOfInstruction" />
            </fo:block>
    </xsl:template>
</xsl:stylesheet>
