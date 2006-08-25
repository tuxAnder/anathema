package net.sf.anathema.character.equipment.item.model;

import net.sf.anathema.framework.itemdata.model.IItemDescription;

public interface IEquipmentTemplateEditModel {

  public IItemDescription getDescription();

  public void loadTemplate(String templateId);

  public boolean isDirty();
}