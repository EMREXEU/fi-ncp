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
                    <fo:table table-layout="fixed" width="100%" font-size="10pt" border-color="black" border-width="1.2pt" border-style="solid">
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
                                <fo:table-cell margin-top="2mm" margin-bottom="2mm" text-align="center" display-align="center">
                                    <fo:block font-size="150%">
                                        Emrex Finland
                                    </fo:block>
                                    <fo:block font-size="120%">
                                        <xsl:value-of select="learner/givenNames"/>&#x00A0;<xsl:value-of select="learner/familyName"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right" display-align="center" padding-right="2mm">
                                    <fo:block display-align="before" space-before="4mm">Page <fo:page-number/> of <fo:page-number-citation ref-id="end-of-document"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body" border-collapse="collapse" reference-orientation="0">
                    <fo:block>ELMO REPORT</fo:block>
                    <fo:table table-layout="fixed" width="100%" border-collapse="separate" font-size="10pt" border-color="black" border-width="1.2pt" border-style="solid" text-align="left" display-align="before" space-after="5mm">
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
                    <fo:block text-align="end" font-size="85%">*Language of Instruction</fo:block>
                    <fo:table table-layout="fixed" width="100%" font-size="10pt" text-align="left" display-align="before" space-after="2mm">
                        <fo:table-column column-width="proportional-column-width(22)"/>
                        <fo:table-column column-width="proportional-column-width(26)"/>
                        <fo:table-column column-width="proportional-column-width(10)"/>
                        <fo:table-column column-width="proportional-column-width(8)"/>
                        <fo:table-column column-width="proportional-column-width(6)"/>
                        <fo:table-column column-width="proportional-column-width(8)"/>
                        <fo:table-column column-width="proportional-column-width(8)"/>
                        <fo:table-column column-width="proportional-column-width(12)"/>
                        <fo:table-header border-color="black" border-width="1.2pt" border-style="solid" >
                            <fo:table-row>
                                <fo:table-cell padding="2px">
                                    <fo:block font-weight="bold">Title</fo:block>
                                    <fo:block>&#x00A0;</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2px">
                                    <fo:block font-weight="bold">Identifier</fo:block>
                                    <fo:block><xsl:value-of select="report/learningOpportunitySpecification/identifier/@type" /></fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2px">
                                    <fo:block font-weight="bold">Type</fo:block>
                                    <fo:block>&#x00A0;</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2px">
                                    <fo:block font-weight="bold">ISCED</fo:block>
                                    <fo:block font-weight="bold">Code</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2px">
                                    <fo:block font-weight="bold">Lang*</fo:block>
                                    <fo:block>&#x00A0;</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2px">
                                    <fo:block font-weight="bold">Result</fo:block>
                                    <fo:block>&#x00A0;</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2px">
                                    <fo:block font-weight="bold">Credits</fo:block>
                                    <fo:block><xsl:value-of select="report/learningOpportunitySpecification/specifies/learningOpportunityInstance/credit/scheme" /></fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2px">
                                    <fo:block font-weight="bold">Registration</fo:block>
                                    <fo:block font-weight="bold">Date</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body font-size="95%">
                            <xsl:for-each select="report/learningOpportunitySpecification">
                                <xsl:sort select="title"/>
                                <xsl:call-template name="los1">
                                    <xsl:with-param name="los" select="."/>
                                </xsl:call-template>
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
            <fo:table-cell padding="2px">
                <fo:block><xsl:value-of select="$los/title"/></fo:block>
            </fo:table-cell>
            <fo:table-cell padding="2px">
                <fo:block><xsl:value-of select="$los/identifier"/></fo:block>
            </fo:table-cell>
            <fo:table-cell padding="2px">
                <fo:block><xsl:value-of select="$los/type" /></fo:block>
            </fo:table-cell>
            <fo:table-cell padding="2px">
                <fo:block><xsl:value-of select="$los/iscedCode" /></fo:block>
            </fo:table-cell>
            <fo:table-cell padding="2px">
                <fo:block><xsl:value-of select="$los/specifies/learningOpportunityInstance/languageOfInstruction" /></fo:block>
            </fo:table-cell>
            <fo:table-cell padding="2px">
                <xsl:variable name="result" select="$los/specifies/learningOpportunityInstance/resultLabel" />
                <xsl:choose>
                    <xsl:when test="$result != ''">
                        <fo:block>
                            <xsl:value-of select="$result"/>
                        </fo:block>
                    </xsl:when>
                    <xsl:otherwise>
                        <fo:block>&#x00A0;</fo:block>
                    </xsl:otherwise>
                </xsl:choose>
            </fo:table-cell>
            <fo:table-cell padding="2px">
                <fo:block><xsl:value-of select="$los/specifies/learningOpportunityInstance/credit/value" /></fo:block>
            </fo:table-cell>
            <fo:table-cell padding="2px">
                <fo:block>
                    <xsl:call-template name="formatdate">
                        <xsl:with-param name="DateTimeStr" select="$los/specifies/learningOpportunityInstance/date"/>
                    </xsl:call-template>
                </fo:block>
            </fo:table-cell>
    </xsl:template>

    <xsl:template name="los1">
        <xsl:param name="los" />
        <fo:table-row border-color="black" border-width="1pt" border-style="solid">
            <xsl:call-template name="los">
                <xsl:with-param name="los" select="."/>
            </xsl:call-template>
        </fo:table-row>
        <xsl:if test="$los/hasPart">
            <xsl:for-each select="$los/hasPart/learningOpportunitySpecification">
                <xsl:sort select="title"/>
                <xsl:call-template name="los2">
                    <xsl:with-param name="los" select="."/>
                </xsl:call-template>
            </xsl:for-each>
      </xsl:if>
    </xsl:template>

    <xsl:template name="los2">
        <xsl:param name="los" />
        <fo:table-row margin-left="2mm" border-color="black" border-width="1pt" border-style="solid">
            <xsl:call-template name="los">
                <xsl:with-param name="los" select="."/>
            </xsl:call-template>
        </fo:table-row>
        <xsl:if test="$los/hasPart">
            <xsl:for-each select="$los/hasPart/learningOpportunitySpecification">
                <xsl:sort select="title"/>
                <xsl:call-template name="los3">
                    <xsl:with-param name="los" select="."/>
                </xsl:call-template>
            </xsl:for-each>
      </xsl:if>
    </xsl:template>

    <xsl:template name="los3">
        <xsl:param name="los" />
        <fo:table-row margin-left="4mm" border-color="black" border-width="1pt" border-style="solid">
            <xsl:call-template name="los">
                <xsl:with-param name="los" select="."/>
            </xsl:call-template>
        </fo:table-row>
    </xsl:template>
</xsl:stylesheet>
