package com.example.mygymapp.data

/**
 * Repository kapselt den Zugriff auf den Room-DAO für Pläne.
 */
class PlanRepository(
    private val dao: PlanDao
) {
    /** Liefert alle wöchentlichen Pläne als Flow */
    fun getWeeklyPlans() = dao.getPlansByType(PlanType.WEEKLY)

    /** Liefert alle täglichen Pläne als Flow */
    fun getDailyPlans() = dao.getPlansByType(PlanType.DAILY)

    /** Fügt einen Plan hinzu und gibt dessen neue ID zurück */
    suspend fun insertPlan(plan: Plan): Long = dao.insertPlan(plan)

    /** Speichert die Cross-Refs für Plan ↔ Exercise */
    suspend fun insertCrossRefs(refs: List<PlanExerciseCrossRef>) = dao.insertCrossRefs(refs)

    /** Löscht einen Plan inklusive aller Cross-Refs */
    suspend fun deletePlan(plan: Plan) {
        dao.deleteCrossRefsForPlan(plan.planId)
        dao.deletePlan(plan)
    }

    /** Liefert einen Plan mitsamt Übungen */
    suspend fun getPlanWithExercises(id: Long): PlanWithExercises? = dao.getPlanWithExercises(id)
}