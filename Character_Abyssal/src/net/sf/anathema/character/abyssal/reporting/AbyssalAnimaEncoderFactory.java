package net.sf.anathema.character.abyssal.reporting;

import com.lowagie.text.pdf.BaseFont;

import net.sf.anathema.character.reporting.sheet.common.anima.AbstractAnimaEncoderFactory;
import net.sf.anathema.character.reporting.sheet.common.anima.AnimaTableEncoder;
import net.sf.anathema.character.reporting.sheet.util.IPdfTableEncoder;
import net.sf.anathema.lib.resources.IResources;

public class AbyssalAnimaEncoderFactory extends AbstractAnimaEncoderFactory {

  public AbyssalAnimaEncoderFactory(IResources resources, BaseFont basefont, BaseFont symbolBaseFont) {
    super(resources, basefont, symbolBaseFont);
  }

  @Override
  protected IPdfTableEncoder getAnimaTableEncoder() {
    return new AnimaTableEncoder(getResources(), getBaseFont(), getFontSize(), new AbyssalAnimaTableStealthProvider(
        getResources()));
  }
}
