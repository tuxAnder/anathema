package net.sf.anathema.character.presenter;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.text.JTextComponent;

import net.disy.commons.swing.ui.IObjectUi;
import net.disy.commons.swing.ui.ObjectUiListCellRenderer;
import net.sf.anathema.character.generic.caste.ICasteType;
import net.sf.anathema.character.generic.caste.ICasteTypeVisitor;
import net.sf.anathema.character.generic.framework.additionaltemplate.listening.DedicatedCharacterChangeAdapter;
import net.sf.anathema.character.generic.template.ICharacterTemplate;
import net.sf.anathema.character.model.ICharacterStatistics;
import net.sf.anathema.character.model.ITypedDescription;
import net.sf.anathema.character.model.concept.IMotivation;
import net.sf.anathema.character.model.concept.INature;
import net.sf.anathema.character.model.concept.INatureType;
import net.sf.anathema.character.model.concept.IWillpowerRegainingConceptVisitor;
import net.sf.anathema.character.model.concept.NatureProvider;
import net.sf.anathema.character.view.ICharacterConceptAndRulesViewFactory;
import net.sf.anathema.character.view.concept.ICharacterConceptAndRulesView;
import net.sf.anathema.character.view.concept.ICharacterConceptAndRulesViewProperties;
import net.sf.anathema.lib.compare.I18nedIdentificateSorter;
import net.sf.anathema.lib.control.change.IChangeListener;
import net.sf.anathema.lib.control.objectvalue.IObjectValueChangedListener;
import net.sf.anathema.lib.gui.selection.IObjectSelectionView;
import net.sf.anathema.lib.resources.IResources;
import net.sf.anathema.lib.util.IIdentificate;
import net.sf.anathema.lib.workflow.textualdescription.ITextView;
import net.sf.anathema.lib.workflow.textualdescription.ITextualDescription;

public class CharacterConceptAndRulesPresenter {

  private final ICharacterConceptAndRulesView view;
  private final ICharacterStatistics statistics;
  private final IResources resources;

  public CharacterConceptAndRulesPresenter(
      ICharacterStatistics statstics,
      ICharacterConceptAndRulesViewFactory viewFactory,
      IResources resources) {
    this.statistics = statstics;
    this.view = viewFactory.createCharacterConceptView();
    this.resources = resources;
  }

  public TabContent[] init() {
    initRulesPresentation();
    initCastePresentation();
    statistics.getCharacterConcept().getWillpowerRegainingConcept().accept(new IWillpowerRegainingConceptVisitor() {
      public void accept(INature nature) {
        initNaturePresentation(nature);
      }

      public void accept(IMotivation motivation) {
        initMotivationPresentation(motivation);
      }
    });
    initConceptPresentation();
    initExperienceListening();
    initGui();
    return new TabContent[] { new TabContent(resources.getString("CardView.CharacterConcept.Title"), view) }; //$NON-NLS-1$
  }

  private void initExperienceListening() {
    statistics.getCharacterContext().getCharacterListening().addChangeListener(new DedicatedCharacterChangeAdapter() {
      @Override
      public void experiencedChanged(boolean experienced) {
        view.setEnabled(!experienced);
      }
    });
    view.setEnabled(!statistics.isExperienced());
  }

  private void initMotivationPresentation(IMotivation motivation) {
    initTextualDescriptionPresentation(motivation.getDescription(), "Label.Motivation"); //$NON-NLS-1$
  }

  private void initConceptPresentation() {
    initTextualDescriptionPresentation(statistics.getCharacterConcept().getConcept(), "Label.Concept"); //$NON-NLS-1$
  }

  private void initTextualDescriptionPresentation(final ITextualDescription description, String resourceKey) {
    final ITextView textView = view.addLabelTextView(resources.getString(resourceKey));
    textView.addTextChangedListener(new IObjectValueChangedListener<String>() {
      public void valueChanged(String newValue) {
        description.setText(newValue);
      }
    });
    description.addTextChangedListener(new IObjectValueChangedListener<String>() {
      public void valueChanged(String newValue) {
        textView.setText(newValue);
      }
    });
    textView.setText(description.getText());
  }

  private void initGui() {
    view.initGui(new ICharacterConceptAndRulesViewProperties() {
      public String getConceptTitle() {
        return resources.getString("CardView.CharacterConcept.Concept"); //$NON-NLS-1$
      }

      public String getRulesTitle() {
        return resources.getString("CardView.CharacterConcept.Rules");} //$NON-NLS-1$
    });
  }

  private void initNaturePresentation(INature nature) {
    INatureType[] unsortedNatures = NatureProvider.getInstance().getNatures();
    INatureType[] natures = new INatureType[unsortedNatures.length];
    new I18nedIdentificateSorter<INatureType>() {
      @Override
      protected String getString(final IResources sorterResources, INatureType type) {
        return sorterResources.getString("Nature." + type.getId() + ".Name"); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }.sortAscending(unsortedNatures, natures, resources);
    final IObjectSelectionView<INatureType> natureView = view.addConceptObjectSelectionView(
        resources.getString("Label.Nature"), //$NON-NLS-1$
        natures,
        new DefaultListCellRenderer() {
          @Override
          public Component getListCellRendererComponent(
              JList list,
              Object value,
              int index,
              boolean isSelected,
              boolean cellHasFocus) {
            String printName;
            if (value == null) {
              printName = resources.getString("ComboBox.SelectLabel"); //$NON-NLS-1$
            }
            else {
              printName = resources.getString("Nature." + ((INatureType) value).getId() + ".Name"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return super.getListCellRendererComponent(list, printName, index, isSelected, cellHasFocus);
          }
        },
        false);
    final ITypedDescription<INatureType> natureType = nature.getDescription();
    natureView.setSelectedObject(natureType.getType());
    final JTextComponent willpowerConditionLabel = view.addWillpowerConditionView(resources.getString("CharacterConcept.GainWillpower")); //$NON-NLS-1$
    natureView.addObjectSelectionChangedListener(new IObjectValueChangedListener<INatureType>() {
      public void valueChanged(INatureType newValue) {
        natureType.setType(newValue);
      }
    });
    natureType.addChangeListener(new IChangeListener() {
      public void changeOccured() {
        updateNature(natureView, willpowerConditionLabel, natureType.getType());
      }
    });
    updateNature(natureView, willpowerConditionLabel, natureType.getType());
  }

  private void initRulesPresentation() {
    view.addRulesLabel(resources.getString("CharacterType.TextLabel.Intro") + " " + resources.getString(statistics.getCharacterTemplate().getPresentationProperties().getNewActionResource()) + "."); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    view.addRulesLabel(resources.getString("Ruleset.TextLabel.UseIntro") + " " + resources.getString("Ruleset." + statistics.getRules().getId()) + "."); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$    
  }

  private void updateNature(
      final IObjectSelectionView<INatureType> natureView,
      final JTextComponent willpowerConditionLabel,
      INatureType natureType) {
    natureView.setSelectedObject(natureType);
    if (natureType == null) {
      willpowerConditionLabel.setText(null);
      return;
    }
    String willpowerCondition = resources.getString("Nature." + natureType + ".Text"); //$NON-NLS-1$ //$NON-NLS-2$
    if (willpowerCondition == null) {
      willpowerCondition = resources.getString("CharacterConcept.WillpowerCondition.NotSpecified"); //$NON-NLS-1$
    }
    willpowerConditionLabel.setText(willpowerCondition);
  }

  private void initCastePresentation() {
    final ICharacterTemplate template = statistics.getCharacterTemplate();
    if (template.getCasteCollection().getAllCasteTypes().length <= 0) {
      return;
    }
    String casteLabelResourceKey = template.getPresentationProperties().getCasteLabelResource();
    IObjectUi casteUi = new IdentificateObjectUi(resources) {
      @Override
      protected Icon getNonNullIcon(IIdentificate value) {
        //TODO: Caste Icon Handling
        return getResources().getImageIcon(
            statistics.getCharacterTemplate().getPresentationProperties().getSmallCasteIconResource(
                value.getId(),
                template.getEdition().getId()));
      }
    };
    ICasteType< ? extends ICasteTypeVisitor>[] allCasteTypes = template.getCasteCollection().getAllCasteTypes();
    final IObjectSelectionView<ICasteType< ? extends ICasteTypeVisitor>> casteView = view.addConceptObjectSelectionView(
        resources.getString(casteLabelResourceKey),
        allCasteTypes,
        new ObjectUiListCellRenderer(casteUi),
        false);
    final ITypedDescription<ICasteType< ? extends ICasteTypeVisitor>> caste = statistics.getCharacterConcept()
        .getCaste();
    casteView.setSelectedObject(caste.getType());
    casteView.addObjectSelectionChangedListener(new IObjectValueChangedListener<ICasteType< ? extends ICasteTypeVisitor>>() {
      public void valueChanged(ICasteType< ? extends ICasteTypeVisitor> newValue) {
        caste.setType(newValue);
      }
    });
    caste.addChangeListener(new IChangeListener() {
      public void changeOccured() {
        casteView.setSelectedObject(caste.getType());
      }
    });
  }
}