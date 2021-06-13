<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/movimentacoes">
		<html xmlns:example="http://example.com">
			<body>
				<table>
					<tr>
						<th>Value</th>
						<th>Date</th>
						<th>Type</th>
					</tr>
					<xsl:for-each select="movimentacao">
						<tr>
							<th>
								<xsl:value-of select="valor" />
							</th>
							<th>
								<xsl:value-of select="data" />
							</th>
							<th>
								<xsl:value-of select="tipo" />
							</th>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>