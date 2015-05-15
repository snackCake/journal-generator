package com.snackcake.journalgenerator.analysis.model

/**
 * Part of Speech codes, as defined by NYU here: http://cs.nyu.edu/grishman/jet/guide/PennPOS.html
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
sealed trait PartOfSpeech {
  def code: String
  def categoryCode: String
  def description: String
}

case object CoordinatingConjunction extends PartOfSpeech {
  override val code: String = "CC"
  override val categoryCode: String = "CC"
  override val description: String = "Coordinating conjunction"
}

case object CardinalNumber extends PartOfSpeech {
  override val code: String = "CD"
  override val categoryCode: String = code
  override val description: String = "Cardinal number"
}

case object Determiner extends PartOfSpeech {
  override val code: String = "DT"
  override val categoryCode: String = code
  override val description: String = "Determiner"
}

case object ForeignWord extends PartOfSpeech {
  override val code: String = "FW"
  override val categoryCode: String = code
  override val description: String = "Foreign word"
}

case object PrepositionOrSubordinatingConjunction extends PartOfSpeech {
  override val code: String = "IN"
  override val categoryCode: String = code
  override val description: String = "Preposition or subordinating conjunction"
}

case object Adjective extends PartOfSpeech {
  override val code: String = "JJ"
  override val categoryCode: String = code
  override val description: String = "Adjective"
}

case object AdjectiveComparative extends PartOfSpeech {
  override val code: String = "JJR"
  override val categoryCode: String = Adjective.categoryCode
  override val description: String = "Adjective, comparative"
}

case object AdjectiveSuperlative extends PartOfSpeech {
  override val code: String = "JJS"
  override val categoryCode: String = Adjective.categoryCode
  override val description: String = "Adjective, superlative"
}

case object ListItemMarker extends PartOfSpeech {
  override val code: String = "LS"
  override val categoryCode: String = code
  override val description: String = "List item marker"
}

case object Modal extends PartOfSpeech {
  override val code: String = "MD"
  override val categoryCode: String = code
  override val description: String = "Modal"
}

case object NounSingularOrMass extends PartOfSpeech {
  override val code: String = "NN"
  override val categoryCode: String = code
  override val description: String = "Noun, singular or mass"
}

case object NounPlural extends PartOfSpeech {
  override val code: String = "NNS"
  override val categoryCode: String = NounSingularOrMass.categoryCode
  override val description: String = "Noun, plural"
}

case object ProperNounSingular extends PartOfSpeech {
  override val code: String = "NNP"
  override val categoryCode: String = NounSingularOrMass.categoryCode
  override val description: String = "Proper noun, singular"
}

case object ProperNounPlural extends PartOfSpeech {
  override val code: String = "NNPS"
  override val categoryCode: String = NounSingularOrMass.categoryCode
  override val description: String = "Proper noun, plural"
}

case object Predeterminer extends PartOfSpeech {
  override val code: String = "PDT"
  override val categoryCode: String = code
  override val description: String = "Predeterminer"
}

case object PossessiveEnding extends PartOfSpeech {
  override val code: String = "POS"
  override val categoryCode: String = code
  override val description: String = "Possessive ending"
}

case object PersonalPronoun extends PartOfSpeech {
  override val code: String = "PRP"
  override val categoryCode: String = code
  override val description: String = "Personal pronoun"
}

case object PossesivePronoun extends PartOfSpeech {
  override val code: String = "PRP$"
  override val categoryCode: String = PersonalPronoun.categoryCode
  override val description: String = "Possessive pronoun"
}

case object Adverb extends PartOfSpeech {
  override val code: String = "RB"
  override val categoryCode: String = code
  override val description: String = "Adverb"
}

case object AdverbComparative extends PartOfSpeech {
  override val code: String = "RBR"
  override val categoryCode: String = Adverb.categoryCode
  override val description: String = "Adverb, comparative"
}

case object AdverbSuperlative extends PartOfSpeech {
  override val code: String = "RBS"
  override val categoryCode: String = Adverb.categoryCode
  override val description: String = "Adverb, superlative"
}

case object Particle extends PartOfSpeech {
  override val code: String = "RP"
  override val categoryCode: String = code
  override val description: String = "Particle"
}

case object Symbol extends PartOfSpeech {
  override val code: String = "SYM"
  override val categoryCode: String = code
  override val description: String = "Symbol"
}

case object To extends PartOfSpeech {
  override val code: String = "TO"
  override val categoryCode: String = code
  override val description: String = "to"
}

case object Interjection extends PartOfSpeech {
  override val code: String = "UH"
  override val categoryCode: String = code
  override val description: String = "Interjection"
}

case object VerbBaseForm extends PartOfSpeech {
  override val code: String = "VB"
  override val categoryCode: String = code
  override val description: String = "Verb, base form"
}

case object VerbPastTense extends PartOfSpeech {
  override val code: String = "VBD"
  override val categoryCode: String = VerbBaseForm.categoryCode
  override val description: String = "Verb, past tense"
}

case object VerbGerundOrPresentParticiple extends PartOfSpeech {
  override val code: String = "VBG"
  override val categoryCode: String = VerbBaseForm.categoryCode
  override val description: String = "Verb, gerund or present participle"
}

case object VerbPastParticiple extends PartOfSpeech {
  override val code: String = "VBN"
  override val categoryCode: String = VerbBaseForm.categoryCode
  override val description: String = "Verb, past participle"
}

case object VerbNon3rdPersonSingularPresent extends PartOfSpeech {
  override val code: String = "VBP"
  override val categoryCode: String = VerbBaseForm.categoryCode
  override val description: String = "Verb, non-3rd person singular present"
}

case object Verb3rdPersonSingularPresent extends PartOfSpeech {
  override val code: String = "VBZ"
  override val categoryCode: String = VerbBaseForm.categoryCode
  override val description: String = "Verb, 3rd person singular present"
}

case object WhDeterminer extends PartOfSpeech {
  override val code: String = "WDT"
  override val categoryCode: String = code
  override val description: String = "Wh-determiner"
}

case object WhPronoun extends PartOfSpeech {
  override val code: String = "WP"
  override val categoryCode: String = PersonalPronoun.categoryCode
  override val description: String = "Wh-pronoun"
}

case object PossesiveWhPronoun extends PartOfSpeech {
  override val code: String = "WP$"
  override val categoryCode: String = PersonalPronoun.categoryCode
  override val description: String = "Possessive wh-pronoun"
}

case object WhAdverb extends PartOfSpeech {
  override val code: String = "WRB"
  override val categoryCode: String = Adverb.categoryCode
  override val description: String = "Wh-adverb"
}

class PartOfSpeechFactory {

  private lazy val parts: Set[PartOfSpeech] = Set(
    CoordinatingConjunction,
    CardinalNumber,
    Determiner,
    ForeignWord,
    PrepositionOrSubordinatingConjunction,
    Adjective,
    AdjectiveComparative,
    AdjectiveSuperlative,
    ListItemMarker,
    Modal,
    NounSingularOrMass,
    NounPlural,
    ProperNounSingular,
    ProperNounPlural,
    Predeterminer,
    PossessiveEnding,
    PersonalPronoun,
    PossesivePronoun,
    Adverb,
    AdverbComparative,
    AdverbSuperlative,
    Particle,
    Symbol,
    To,
    Interjection,
    VerbBaseForm,
    VerbPastTense,
    VerbGerundOrPresentParticiple,
    VerbPastParticiple,
    VerbNon3rdPersonSingularPresent,
    Verb3rdPersonSingularPresent,
    WhDeterminer,
    WhPronoun,
    PossesiveWhPronoun,
    WhAdverb
  )

  private lazy val partCodeLookup: Map[String, PartOfSpeech] = parts.map(part => part.code -> part).toMap

  private lazy val partCategoryCodeLookup: Map[String, PartOfSpeech] = parts.filter(part => part.categoryCode == part.code)
    .map(part => part.categoryCode -> part)
    .toMap

  def lookupByCode(code: String): Option[PartOfSpeech] = partCodeLookup.get(code)

  def lookupByCategoryCode(categoryCode: String): Option[PartOfSpeech] = partCategoryCodeLookup.get(categoryCode)
}