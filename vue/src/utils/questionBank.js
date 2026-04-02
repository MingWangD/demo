const buildSubjectBank = (subject) => {
  const objective = Array.from({ length: 30 }).map((_, i) => ({
    id: `${subject}-O-${i + 1}`,
    type: 'OBJECTIVE',
    score: 2,
    title: `【${subject}客观题${i + 1}】请完成本题并选择最优答案。`
  }))
  const subjective = Array.from({ length: 20 }).map((_, i) => ({
    id: `${subject}-S-${i + 1}`,
    type: 'SUBJECTIVE',
    score: 10,
    title: `【${subject}主观题${i + 1}】请结合课程知识进行分析与作答。`
  }))
  return [...objective, ...subjective]
}

const COURSE_BANK = {
  '201': buildSubjectBank('数据结构'),
  '202': buildSubjectBank('数据库原理'),
  '203': buildSubjectBank('机器学习导论'),
  default: buildSubjectBank('通用课程')
}

export const getQuestionBank = (courseId) => COURSE_BANK[String(courseId)] || COURSE_BANK.default
