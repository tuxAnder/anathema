package net.sf.anathema.character.reporting.sheet.common.magic.generic;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;

import net.sf.anathema.character.generic.character.IGenericCharacter;
import net.sf.anathema.character.reporting.sheet.common.IPdfContentEncoder;
import net.sf.anathema.character.reporting.util.Bounds;
import net.sf.anathema.lib.resources.IResources;

public class PdfGenericCharmEncoder implements IPdfContentEncoder {
  
  private final IResources resources;
  private final BaseFont baseFont;

  public PdfGenericCharmEncoder(IResources resources, BaseFont baseFont) {
    this.resources = resources;
    this.baseFont = baseFont;
  }
  
  public String getHeaderKey() {
    return "GenericCharms";
  }
  
  public void encode(PdfContentByte directContent, IGenericCharacter character, Bounds bounds) throws DocumentException {
    new PdfGenericCharmTableEncoder(resources, baseFont).encodeTable(directContent, character, bounds);
  }
}