package com.snackcake.journalgenerator.analysis

/**
 * Trait the allows an object to normalize words.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
class WordNormalizer {

  /**
   * @return A map of keys (sets of words) that should all be normalized to their values (single words) when encountered in a title.
   */
  protected def normalizeWordRules = Map(
    Set("and", "&") -> "and"
  )

  /**
   * If the source word matches a normalization rule, return the target word of the rule, otherwise return the source word.
   *
   * @param sourceWord The word to match against the normalization rules
   * @return The normalized word, or the source word if it didn't need to be normalized.
   */
  def normalizeWord(sourceWord: String): String = {
    val lowerSourceWord = sourceWord.toLowerCase
    val matchingRules = normalizeWordRules.keys.filter(_.contains(lowerSourceWord))
    matchingRules.lastOption.fold(sourceWord) {
      normalizeWordRules.get(_).get
    }
  }
}
