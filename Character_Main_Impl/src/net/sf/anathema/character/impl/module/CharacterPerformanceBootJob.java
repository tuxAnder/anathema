package net.sf.anathema.character.impl.module;

import net.sf.anathema.framework.IAnathemaModel;
import net.sf.anathema.framework.view.IAnathemaView;
import net.sf.anathema.initialization.IAnathemaBootJob;
import net.sf.anathema.lib.resources.IResources;

public class CharacterPerformanceBootJob implements IAnathemaBootJob {

  public void run(IResources resources, IAnathemaModel model, IAnathemaView view) {
    new CharacterPerformanceTuner(model, resources).startTuning();
  }
}