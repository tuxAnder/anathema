package net.sf.anathema.test.character.generic.framework.xml;

import net.sf.anathema.character.generic.framework.xml.creation.GenericCreationPoints;
import net.sf.anathema.lib.lang.clone.ICloneable;
import net.sf.anathema.lib.lang.clone.test.AbstractDeepCloneableTest;

public class GenericCreationPointsTest extends AbstractDeepCloneableTest {

  @Override
  protected ICloneable createObjectUnderTest() {
    return new GenericCreationPoints();
  }
}